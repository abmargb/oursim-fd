import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.edu.ufcg.lsd.oursim.OurSimAPIVolatilityTest;
import br.edu.ufcg.lsd.oursim.entities.ProcessorTest;
import br.edu.ufcg.lsd.oursim.entities.TaskTest;
import br.edu.ufcg.lsd.oursim.policy.NoFSharingPolicyTest;
import br.edu.ufcg.lsd.oursim.policy.OurGridPersistentSchedulerMultiplePeersTest;
import br.edu.ufcg.lsd.oursim.policy.OurGridPersistentSchedulerTest;
import br.edu.ufcg.lsd.oursim.policy.OurGridReplicationSchedulerTest;

@RunWith(Suite.class)
@SuiteClasses( { ProcessorTest.class, TaskTest.class, NoFSharingPolicyTest.class, OurGridPersistentSchedulerTest.class, OurSimAPIVolatilityTest.class,
		OurGridReplicationSchedulerTest.class, OurGridPersistentSchedulerMultiplePeersTest.class })
public class TestSuite {

	static class Compatibility {

		static Test suite() throws IOException {
			return new JUnit4TestAdapter(TestSuite.class);
		}
	}

}