package com.human.algorithm;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.graphstream.graph.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.human.main.App;
import com.human.model.OwnEdge;
import com.human.model.OwnGraph;
import com.human.model.OwnNode;

public class Graph01 {
	OwnGraph graph01;
	ShortestPath shortestPath;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		File file = new File("");
		String fileName = "graph01.gka";
		try {
		    ClassLoader classLoader = App.class.getClassLoader();
		    file = new File(classLoader.getResource(fileName).getFile());
		}
		catch (Exception e) {
			System.out.println("graph01.gka cannot found!");
		}
		graph01 = OwnGraph.getInstanceFromFile(file);
		shortestPath = new DijekstraBFS(graph01);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	// Testet ob der isolierte Knoten "n" unereichbar ist
	public void nNotReachableTest() {
		OwnNode n = graph01.getNode("n");
		shortestPath.run(n);
		boolean allPathEmpty = graph01.realNodes()
			.filter(node -> !n.equals(node))
			.allMatch(node -> shortestPath.getShortestPath(node).getEdgeCount() == 0);
		System.out.println(allPathEmpty);
		assertTrue(allPathEmpty);
	}


	@Test
	// Testet ob die reflexive Kante vom Knoten "i" nicht genutzt wird
	public void reflexiveTest() {
		OwnNode i = graph01.getNode("i");
		shortestPath.run(i);
		int costs = shortestPath.getShortestPathValue(i);
		int expected = 0;
		assertEquals(costs, expected);
	}

	@Test
	// Testet ob die gerichtete Kante h -> b nicht genutzt wird, wenn kürzester Pfad von b nach h gesucht wird. 
	public void noInverseDirectedTest() {
		OwnNode h = graph01.getNode("h");
		OwnNode b = graph01.getNode("b");
		shortestPath.run(b);
		Path path = shortestPath.getShortestPath(h);
		assertTrue(path.size() > 1);
	}
}
