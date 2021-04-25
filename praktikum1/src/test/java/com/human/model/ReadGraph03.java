/**
 * 
 */
package com.human.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.human.main.App;
import com.human.model.OwnGraph;

/**
 * @author human
 *
 */
public class ReadGraph03 {
	
	public OwnGraph graph03;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// Lädt Graph01 hoch
		File file = new File("");
		String fileName = "graph03.gka";
		try {
		    ClassLoader classLoader = App.class.getClassLoader();
		    file = new File(classLoader.getResource(fileName).getFile());
		}
		catch (Exception e) {
			System.out.println("graph01.gka cannot found!");
		}
		graph03 = OwnGraph.getInstanceFromFile(file);
	}


	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Zeile: M�nster -- Bremen :         173; 
	 * soll nicht akzeptiert werden!
	 */
	@Test
	public void testNoMuensterBremenConnection() {
		OwnNode muenster = graph03.getNode("M�nster");
		OwnNode bremen = graph03.getNode("Bremen");
		boolean muensterToBremen = muenster.realEdges()
				.anyMatch(e -> !e.getOpposite(muenster).equals(bremen));
		assertTrue(muensterToBremen);
		boolean bremenToMuenster = bremen.realEdges()
				.anyMatch(e -> !e.getOpposite(bremen).equals(muenster));
		assertTrue(bremenToMuenster);
	}
}