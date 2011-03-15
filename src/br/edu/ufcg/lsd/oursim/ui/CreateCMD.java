package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import br.edu.ufcg.lsd.oursim.util.ArrayBuilder;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class CreateCMD {

	// cd /tmp;scp cororoca:~/workspace/OurSim/cmd.txt .; time sh cmd.txt
	public static void main(String[] args) throws IOException {

		final String NL = " \\\n ";
		final String NC = " && " + NL;

		String setUp = "";
		setUp += "cd /tmp " + NC;
		setUp += "  rm -rf playpen " + NC;
		setUp += "  mkdir -p playpen/oursim " + NC;
		setUp += "  scp cororoca:~/workspace/OurSim/dist/oursim.zip . " + NC;
		setUp += "  unzip -o oursim.zip -d playpen/oursim " + NC;
		setUp += "  scp cororoca:~/workspace/SpotInstancesSimulator/dist/spotsim.zip . " + NC;
		setUp += "  unzip -o spotsim.zip -d playpen/oursim " + NC;
		setUp += "  cd playpen/oursim " + NL;

		String cmd = "";
		String sep = "";
		// cmd += setUp;

		String workloadType = "marcus";
		String workloadPattern = "%s_workload_7_dias_%s_sites_%s.txt";

		String resultDir = "/local/edigley/traces/oursim/28_02_2001_new_marcus_workload";

		long avDur = TimeUtil.ONE_WEEK + TimeUtil.ONE_DAY;

		int[] spotLimits = new int[] { 100, Integer.MAX_VALUE, 500, 400, 300, 200 };//, 80, 60, 40, 20 };

		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "2" : "";

		String java = " $JAVACALL ";
		String jvmArgs = "";
		cmd += String.format("JAVACALL='java %s -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar' " + NC, jvmArgs);

		boolean groupedbypeer = false;

		boolean utilization = false;

		boolean runOurSim = true;
		
		boolean runSpotSim = false ;
		
		// adicionar a opção nof como uma booleana

		String[] spts = new String[] {

//		"us-east-1.linux.m1.small.csv",
//
//		"us-east-1.linux.m1.large.csv",
//
//		"us-east-1.linux.m1.xlarge.csv",

		"us-east-1.linux.c1.medium.csv",

//		"us-east-1.linux.c1.xlarge.csv",
//
//		"us-east-1.linux.m2.xlarge.csv",
//
//		"us-east-1.linux.m2.2xlarge.csv",
//
//		"us-east-1.linux.m2.4xlarge.csv"

		};

		if (!runSpotSim) {
			spts = new String[] {};
		}
		int[] nSitesV = new int[] { 30 };
		int[] nResV = new int[] { 20 };

		int[] rodadas = ArrayBuilder.createVector(1);

		System.out.println("scheduler: " + scheduler);
		System.out.print("nSitesV  : ");
		ArrayBuilder.print(nSitesV);
		System.out.print("nResV    : ");
		ArrayBuilder.print(nResV);
		System.out.print("rodadas  : ");
		ArrayBuilder.print(rodadas);

		String inputDir = "input-files/";
		List<String> inputs = new ArrayList<String>();
		List<String> outputs = new ArrayList<String>();

		List<WraperTask> tasks = new ArrayList<WraperTask>();

		// tá variando primeiro. Talvez fosse melhor se nSites variasse primeiro
		for (int rodada : rodadas) {
			// for (String sptFilePath : spts) {
			for (int nSites : nSitesV) {
				for (int nRes : nResV) {
					String isdFilePath = String.format("iosup_site_description_%s_sites.txt", nSites);
					String mdFilePath = String.format("machines_speeds_%s_sites_%s_machines_by_site_%s.txt", nSites, nRes, rodada);
					String spt = " $SPT ";
					String isd = " $ISD ";
					String md = " $MD ";

					WraperTask oursimTask = new WraperTask();

					String wFile = String.format(workloadPattern, workloadType, nSites, rodada);
					oursimTask.inputs.add(wFile);
					oursimTask.inputs.add(isdFilePath);
					oursimTask.inputs.add(mdFilePath);
					oursimTask.labels.put(isd.trim(), inputDir + isdFilePath);
					oursimTask.labels.put(md.trim(), inputDir + mdFilePath);
					oursimTask.labels.put(java.trim(), "unzip oursim.zip ; java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar ");

					String oursimTrace = String.format("oursim-trace-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);
					oursimTask.outputs.add(oursimTrace);
					if (runOurSim) {
						outputs.add(oursimTrace);
					}
					String uFile = String.format("oursim-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);
					if (utilization) {
						oursimTask.outputs.add(uFile);
						outputs.add(uFile);
					}
					String oursimPattern = java + "oursim.jar -w %s -wt %s -s %s %s -pd %s -nr %s -synthetic_av ourgrid %s -o %s %s -md %s";// XXX
					// -erw
					// %s";
					String preSpotWorkload = oursimTrace + "_spot_workload.txt";
					preSpotWorkload = "";// XXX
					oursimTask.cmd = String.format(oursimPattern, inputDir + wFile, workloadType, scheduler, nReplicas, isd, nRes, avDur, oursimTrace,
							utilization ? " -u " + uFile : "", md, preSpotWorkload);

					WraperTask prespotsimTask = new WraperTask();

					oursimTask.outputs.add(preSpotWorkload);
					prespotsimTask.inputs.add(preSpotWorkload);

					String spotWorkload = oursimTrace + "_spot_workload_sorted.txt";
					spotWorkload = wFile;// XXX
					// prespotsimTask.outputs.add(spotWorkload);
					// outputs.add(spotWorkload);//XXX
					// String spotsimPrePattern = "sort -g %s > %s ";
					// prespotsimTask.cmd = String.format(spotsimPrePattern,
					// preSpotWorkload, spotWorkload);

					if (runOurSim) {
						tasks.add(oursimTask);
					}
					List<WraperTask> spotTasks = new ArrayList<WraperTask>();

					for (String sptFilePath : spts) {
						for (int spotLimit : spotLimits) {

							WraperTask spotsimTask = new WraperTask();
							spotsimTask.inputs.add(spotWorkload);
							spotsimTask.inputs.add(sptFilePath);
							spotsimTask.inputs.add(isdFilePath);
							spotsimTask.inputs.add(mdFilePath);

							String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt",
									scheduler, nRes, nSites, spotLimit, groupedbypeer, sptFilePath, rodada);
							spotsimTask.outputs.add(spotsimTrace);
							outputs.add(spotsimTrace);
							String uSpotFile = String.format(
									"spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt", scheduler, nRes,
									nSites, spotLimit, groupedbypeer, sptFilePath, rodada);
							if (utilization) {
								spotsimTask.outputs.add(uSpotFile);
								outputs.add(uSpotFile);
							}
							String spotsimPattern = java + "spotsim.jar -spot %s -l %s -bid max -w %s -av %s -o %s %s -pd %s -md %s";
							String gdp = groupedbypeer ? "-gbp" : "";

							spt = inputDir + sptFilePath;
							spotsimTask.cmd = String.format(spotsimPattern, gdp, spotLimit, inputDir + spotWorkload, spt, spotsimTrace, utilization ? " -u "
									+ uSpotFile : "", isd, md);

							inputs.add(wFile);
							inputs.add(sptFilePath);
							inputs.add(isdFilePath);
							inputs.add(mdFilePath);

							// inputs.add(spotWorkload);//XXX

							spotTasks.add(spotsimTask);
						}

					}

					inputs.add(wFile);// XXX onlyOurSim
					inputs.add(isdFilePath);// XXX onlyOurSim
					inputs.add(mdFilePath);// XXX onlyOurSim

					StringBuilder sb = new StringBuilder("#site	num_cpus\n");
					for (int i = 1; i <= nSites; i++) {
						sb.append(i).append(" ").append(nRes).append("\n");
					}
					FileUtils.writeStringToFile(new File("/local/edigley/traces/oursim/sites_description/" + isdFilePath), sb.toString());

					String tearDown = "scp";
					String tearDownSep = " ";
					for (String output : outputs) {
						tearDown += tearDownSep + output;
						tearDownSep = NL;
					}
					tearDown += " cororoca:" + resultDir;
					outputs.clear();

					String shellVarDef = String.format("ISD=%s && \\\n MD=%s \\\n", inputDir + isdFilePath, inputDir + mdFilePath);
					cmd += NL + sep + shellVarDef;
					
					if (runOurSim) {
						cmd += " && " +  oursimTask;// XXX + NC +
						// prespotsimTask;
					}
					
					for (WraperTask spotsimTask : spotTasks) {
						cmd += NC + spotsimTask;
					}
					cmd += NC + tearDown;
					sep = NC;

				}
			}
			// }
		}

		FileUtils.deleteQuietly(new File(inputDir));

		(new File(inputDir)).mkdir();

		FileUtils.copyFileToDirectory(new File("/home/edigley/local/resources_BKP/exemplo-de-execucao.txt"), new File(inputDir));

		for (String inputFile : inputs) {
			String sourceDir = null;

			if (inputFile.endsWith(".txt_spot_workload_sorted.txt")) {
				sourceDir = "/local/edigley/traces/oursim/03_12_2010/";
			} else if (inputFile.startsWith(workloadType + "_workload")) {
				sourceDir = "/local/edigley/traces/oursim/workloads/";
				sourceDir = "/local/edigley/traces/oursim/marcus/new_workload/teste_geracao_de_workload/";
			} else if (inputFile.startsWith("machines_speeds_") || inputFile.startsWith("iosup_site_description_")) {
				sourceDir = "/local/edigley/traces/oursim/sites_description/";
			} else if (inputFile.endsWith(".csv")) {
				sourceDir = "/local/edigley/traces/spot_instances/spot-instances-prices-23-11-2010/";
			}
			File iFile = new File(sourceDir + inputFile);
			if (iFile.exists()) {
				FileUtils.copyFileToDirectory(iFile, new File(inputDir));
			} else {
				System.out.println("Arquivo não existente: " + iFile.getAbsolutePath());
				System.exit(1);
			}
		}

		FileUtils.writeStringToFile(new File("cmd.txt"), setUp + NC + cmd);

		StringBuilder jobSB = new StringBuilder();
		jobSB.append("job : \n");
		jobSB.append("\tlabel : oursim \n");
		jobSB.append("\trequirements : ( os == linux )\n\n");
		

		for (WraperTask task : tasks) {

			jobSB.append("\ttask : \n");
			jobSB.append("\t\tinit : store /local/edigley/workspace/OurSim/dist/oursim.zip oursim.zip \n");

			jobSB.append("\t\tremote : ");
			for (Entry<String, String> entry : task.labels.entrySet()) {
				task.cmd = task.cmd.replace(entry.getKey(), entry.getValue());
			}

			jobSB.append(task.cmd + "\n");

			jobSB.append("\t\tfinal : ");
			for (String output : task.outputs) {
				if (!output.trim().isEmpty()) {
					jobSB.append(String.format("\t\t\tget %1$s /local/edigley/traces/oursim/job_12_02_2011/%1$s \n", output));
				}
			}

		}

		FileUtils.writeStringToFile(new File("oursim.jdf"), jobSB.toString());

		System.out.println("\n  Finished!!!!");

	}
}
