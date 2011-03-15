package br.edu.ufcg.lsd.oursim.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;

public class TaskTest {

	@Test
	public void testUpdateProcessing() {

		Peer p = new Peer("", FifoSharingPolicy.getInstance());
		p.addMachine(new Machine("",Processor.EC2_COMPUTE_UNIT.getSpeed()));
		Job j = new Job(1,0,p);

		Task Task = new Task(0, "executavel.exe", 30, 0, j);

		Processor processor = new Processor(0, 500);

		TaskExecution taskExecution = new TaskExecution(Task, processor, 0);

		assertEquals(new Long(130), taskExecution.updateProcessing(50));

		Processor processor2 = new Processor(0, 200);
		taskExecution.setProcessor(processor2);
		assertEquals(new Long(295), taskExecution.updateProcessing(80));

		Processor processor3 = new Processor(0, 1000);
		taskExecution.setProcessor(processor3);
		assertEquals(new Long(9), taskExecution.updateProcessing(130));
		taskExecution.setProcessor(processor);
		assertEquals(new Long(9), taskExecution.updateProcessing(139));

		assertEquals(new Long(0), taskExecution.updateProcessing(148));

	}

}