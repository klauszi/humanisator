/**
 * 
 */
package com.human.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
public class ReadGraph11 {
	
	public OwnGraph graph11;

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
		String fileName = "graph11.gka";
		try {
		    ClassLoader classLoader = App.class.getClassLoader();
		    file = new File(classLoader.getResource(fileName).getFile());
		}
		catch (Exception e) {
			System.out.println("graph01.gka cannot found!");
		}
		graph11 = OwnGraph.getInstanceFromFile(file);
	}


	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * keine Mehrfachkanten
	 */
	@Test
	public void noMultiEdges() {
		List<List<String>>neighborlists = graph11.realNodes()
				.map(n -> n.neighborNodes()
						.map(neighbor -> neighbor.getId())
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
		for(List<String> neighbors: neighborlists) {
			// System.out.println(neighbors);
			Set<String> uniqueNeighbors = new HashSet<String>(neighbors);
			assertTrue(uniqueNeighbors.size() == neighbors.size());
		}
	}
}