package br.edu.ufcg.lsd.oursim;

import java.io.IOException;

import junit.framework.TestCase;

import org.designwizard.design.ClassNode;
import org.designwizard.exception.InexistentEntityException;
import org.designwizard.exception.NotAnInterfaceException;
import org.designwizard.main.DesignWizard;

public class DesignTest extends TestCase {

	public void testCommonPackage() throws IOException, InexistentEntityException, NotAnInterfaceException {

		DesignWizard dw = new DesignWizard("/local/edigley/workspace/OurSim/dist/OurSim/oursim.jar");

		ClassNode commonInterface = dw.getClass("br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener");

		for (ClassNode c : commonInterface.getEntitiesThatImplements()) {

			assertTrue(c.getPackage().equals(dw.getPackage("br.edu.ufcg.lsd.oursim.policy")));

		}

	}

}