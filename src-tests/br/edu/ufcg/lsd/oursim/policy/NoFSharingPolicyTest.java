package br.edu.ufcg.lsd.oursim.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.entities.TaskExecution;

public class NoFSharingPolicyTest {

	private NoFSharingPolicy nof;
	private Peer p1;
	private Peer p2;
	private Peer p3;
	private Peer p4;

	@Before
	public void setUp() throws Exception {
		nof = NoFSharingPolicy.getInstance();
		p1 = new Peer("p1", 10, 1, nof);
		p2 = new Peer("p2", 10, 1, nof);
		p3 = new Peer("p3", 10, 1, nof);
		p4 = new Peer("p4", 10, 1, nof);
	}

	// No balance between peers yet
	@Test
	public final void testOnePeerNoBalance() {
		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 10);
		Job job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 10; id++) {
			Task Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p3, resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p2, preemptablePeers.get(0));

	}

	// No balance, two peers, one consuming more resources
	@Test
	public final void testTwoPeersSameBalanceOneUsingMoreResources() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 4);
		resourcesBeingConsumed.put(p3, 6);

		Job job;

		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 4; id++) {
			Task Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 6; id++) {
			Task Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p4, resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p3, preemptablePeers.get(0));
	}

	// No balance, two peers using the same amount of resources, recently job
	// first
	@Test
	public final void testTwoPeersSameBalanceSameResourceUseOneWithMostRecentJob() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 5);
		resourcesBeingConsumed.put(p3, 5);

		Job job;

		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			Task Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 4; id++) {
			Task Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		Task Task = new Task(4, "", 10, 10, job);
		Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
		Task.setStartTime(20);
		runningElements.add(Task);

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p4, resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p3, preemptablePeers.get(0));
	}

	@Test
	public final void testTwoPeersDifferentBalances() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 5);
		resourcesBeingConsumed.put(p3, 5);

		Job job = new Job(0, 10, p1);

		Task Task = new Task(4, "", 10, 0, job);
		Machine m = new Machine("m_1",Processor.EC2_COMPUTE_UNIT.getSpeed());
		Task.setTaskExecution(new TaskExecution(Task,m.getDefaultProcessor(),0));
		Task.setStartTime(0);
		Task.finish(10);

		nof.increaseBalance(p1, p2, Task);

		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p2, resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p3, preemptablePeers.get(0));
	}

	@Test
	public final void testTwoPeersSameBalances() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 5);
		resourcesBeingConsumed.put(p3, 5);

		Job job = new Job(0, 10, p1);

		Task Task = new Task(4, "", 10, 0, job);
		Machine m = new Machine("m_1",Processor.EC2_COMPUTE_UNIT.getSpeed());
		Task.setTaskExecution(new TaskExecution(Task,m.getDefaultProcessor(),10));
		Task.setStartTime(0);
		Task.finish(10);

		nof.increaseBalance(p1, p2, Task);
		nof.increaseBalance(p1, p3, Task);

		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			Task = new Task(id, "", 10, 10, job);
			Task.setTaskExecution(new TaskExecution(Task,new Machine("m_bla",Processor.EC2_COMPUTE_UNIT.getSpeed()).getDefaultProcessor(),10));
			Task.setStartTime(10);
			runningElements.add(Task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p2, resourcesBeingConsumed, runningElements);

		assertTrue(preemptablePeers.isEmpty());
	}

}
