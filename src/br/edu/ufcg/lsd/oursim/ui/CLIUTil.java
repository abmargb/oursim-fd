package br.edu.ufcg.lsd.oursim.ui;

import java.text.DecimalFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.io.output.ComputingElementEventCounter;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.TaskPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.WorkerPrintOutput;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public class CLIUTil {

	public static boolean hasOptions(CommandLine cmd, String... options) {
		for (String option : options) {
			if (!cmd.hasOption(option)) {
				return false;
			}
		}
		return true;
	}

	public static CommandLine parseCommandLine(String[] args, Options options, String HELP, String USAGE, String EXECUTION_LINE) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			showMessageAndExit(e);
		}

		checkForHelpAsk(options, cmd, HELP, USAGE, EXECUTION_LINE);

		return cmd;
	}

	public static void showMessageAndExit(Exception e) {
		showMessageAndExit(e.getMessage());
	}

	public static void showMessageAndExit(String message) {
		System.err.println(message);
		System.exit(1);
	}

	public static String getSummaryStatistics(ComputingElementEventCounter c, String instance, String group, boolean groupedCloudUser, int nPeers,
			int nMachines, double utilization, double realUtilization, long simulationDuration) {

		DecimalFormat dft = new DecimalFormat("###0.0000");

		// submitted - (finished + preempted)
		int notStarted = c.getNumberOfSubmittedJobs() - (c.getNumberOfFinishedJobs() + c.getNumberOfPreemptionsForAllJobs());

		StringBuilder sb = new StringBuilder(
				"# submitted finished preempted notStarted submittedTasks finishedTasks success sumOfJobsMakespan sumOfTasksMakespan finishedCost preemptedCost totalCost costByTask nPeers nMachines instance group groupedCloudUser utilization realUtilization simulationDuration"
						+ "\n");
		double totalCost = c.getTotalCostOfAllFinishedJobs() + c.getTotalCostOfAllPreemptedJobs();
		sb.append("# " +

		c.getNumberOfSubmittedJobs()).append(" ")

		.append(c.getNumberOfFinishedJobs()).append(" ")

		.append(c.getNumberOfPreemptionsForAllJobs()).append(" ")

		.append(notStarted).append(" ")

		.append(c.getNumberOfSubmittedTasks()).append(" ")

		.append(c.getNumberOfFinishedTasks()).append(" ")

		.append(dft.format(c.getNumberOfFinishedJobs() / (c.getNumberOfSubmittedJobs() * 1.0)).replace(",", ".")).append(" ")

		.append(c.getSumOfJobsMakespan()).append(" ")

		.append(c.getSumOfTasksMakespan()).append(" ")

		.append(dft.format(c.getTotalCostOfAllFinishedJobs()).replace(",", ".")).append(" ")

		.append(dft.format(c.getTotalCostOfAllPreemptedJobs()).replace(",", ".")).append(" ")

		.append(dft.format(totalCost).replace(",", ".")).append(" ")

		.append(dft.format(totalCost / (c.getNumberOfFinishedTasks() * 1.0)).replace(",", ".")).append(" ")

		.append(nPeers).append(" ")

		.append(nMachines).append(" ")

		.append(instance).append(" ")

		.append(group).append(" ")

		.append(String.valueOf(groupedCloudUser).toUpperCase()).append(" ")

		.append(utilization).append(" ")

		.append(realUtilization).append(" ")

		.append(simulationDuration);

		return sb.toString();
	}

	public static String formatSummaryStatistics(ComputingElementEventCounter c, String instance, String group, boolean groupedCloudUser, int nPeers,
			int nMachines, double utilization, double realUtilization, long simulationDuration) {

		DecimalFormat dft = new DecimalFormat("000.00");

		String resume = "";

		resume += "# Total of submitted            jobs: " + c.getNumberOfSubmittedJobs() + ".\n";
		resume += "# Total of finished             jobs: " + c.getNumberOfFinishedJobs() + ".\n";
		resume += "# Total of preemptions for all  jobs: " + c.getNumberOfPreemptionsForAllJobs() + ".\n";
		resume += "# Total of finished            tasks: " + c.getNumberOfFinishedTasks() + ".\n";
		resume += "# Total of preemptions for all tasks: " + c.getNumberOfPreemptionsForAllTasks() + ".\n";
		resume += "# Total cost of all finished    jobs: " + dft.format(c.getTotalCostOfAllFinishedJobs()) + ".\n";
		resume += "# Total cost of all preempted   jobs: " + dft.format(c.getTotalCostOfAllPreemptedJobs()) + ".\n";
		resume += "# Total cost of all             jobs: " + dft.format(c.getTotalCostOfAllFinishedJobs() + c.getTotalCostOfAllPreemptedJobs()) + ".\n";
		resume += "# Total of                    events: " + EventQueue.totalNumberOfEvents + ".\n";
		resume += getSummaryStatistics(c, instance, group, groupedCloudUser, nPeers, nMachines, utilization, realUtilization, simulationDuration);
		return resume;
	}

	public static ComputingElementEventCounter prepareOutputAccounting(CommandLine cmd, boolean verbose) {
		ComputingElementEventCounter computingElementEventCounter = new ComputingElementEventCounter();

		if (verbose) {
			JobEventDispatcher.getInstance().addListener(new PrintOutput());
			TaskEventDispatcher.getInstance().addListener(new TaskPrintOutput());
			WorkerEventDispatcher.getInstance().addListener(new WorkerPrintOutput());
			EventQueue.LOG = true;
			EventQueue.LOG_FILEPATH = "events_oursim.txt";
		}

		JobEventDispatcher.getInstance().addListener(computingElementEventCounter);
		TaskEventDispatcher.getInstance().addListener(computingElementEventCounter);

		return computingElementEventCounter;
	}

	public static void treatWrongCommand(Options options, CommandLine cmd, String HELP, String USAGE, String EXECUTION_LINE) {
		System.err.println("Informe todos os parâmetros obrigatórios.");
		HelpFormatter formatter = new HelpFormatter();
		if (cmd.hasOption(HELP)) {
			formatter.printHelp(EXECUTION_LINE, options);
		} else if (cmd.hasOption(USAGE)) {
			formatter.printHelp(EXECUTION_LINE, options, true);
		} else {
			formatter.printHelp(EXECUTION_LINE, options, true);
		}
		System.exit(1);
	}

	public static void checkForHelpAsk(Options options, CommandLine cmd, String HELP, String USAGE, String EXECUTION_LINE) {
		HelpFormatter formatter = new HelpFormatter();
		if (cmd.hasOption(HELP)) {
			formatter.printHelp(EXECUTION_LINE, options);
		} else if (cmd.hasOption(USAGE)) {
			formatter.printHelp(EXECUTION_LINE, options, true);
		} else {
			return;
		}
		System.exit(1);
	}

}
