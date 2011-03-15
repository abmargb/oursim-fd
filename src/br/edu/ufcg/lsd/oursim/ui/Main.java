package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;

import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//String workload = "resources/marcus_workload_1_dias_10_sites_1.txt";
		//String workload = "/home/edigley/local/traces/oursim/marcus/new_workload/teste_geracao_de_workload/marcus_workload_7_dias_30_sites_1.txt";
		String workload = "input-files/head_marcus_workload_7_dias_30_sites_1.txt";
		String peersDescription = "input-files/iosup_site_description_30_sites.txt";
		String machinesDescription = "input-files/machines_speeds_30_sites_20_machines_by_site_1.txt";
		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "3" : "";
		String schedulerOption = scheduler + " " + nReplicas;

		String workloadType = "marcus";
		String avDuration = "ourgrid";//String.valueOf(TimeUtil.ONE_WEEK + TimeUtil.ONE_DAY);

		String output = "oursim-trace-20_7_dias_30_sites.txt";
		String utilizationFile = "oursim_system_utilization.txt";
		String workerEventsFile = "oursim_worker_events.txt";
		String taskEventsFile = "oursim_task_events.txt";

		String u = "";
		String te = "";
		String we = "";

		/* Descomente as linhas abaixo se quiser rastrear os respectivos eventos */
		/* -u <file> : a cada evento registra a utilizacao do sistem */
		// u = String.format("-u %s", utilizationFile);
		/* -te <file> : registra todos os eventos envolvendo as tasks */
		// te = String.format("-te %s", taskEventsFile);
		/* -we <file> : registra todos os eventos de (in)disponibilidade */
		// we = String.format("-we %s", workerEventsFile);
		String optional = String.format(" %s %s %s ", u, te, we);

		String argsPattern = "-h 20000 -wt %s -w %s -s %s -pd %s -md %s -synthetic_av %s -o %s %s";

		String argsString = String.format(argsPattern, workloadType, workload, schedulerOption, peersDescription, machinesDescription, avDuration, output,
				optional);

		args = argsString.split("\\s+");
		// System.out.println(argsString);
		CLI.main(args);
	}

}
