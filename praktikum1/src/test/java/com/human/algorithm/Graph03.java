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

public class Graph03 {
	OwnGraph graph03;
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
		String fileName = "graph03.gka";
		try {
		    ClassLoader classLoader = App.class.getClassLoader();
		    file = new File(classLoader.getResource(fileName).getFile());
		}
		catch (Exception e) {
			System.out.println("graph01.gka cannot found!");
		}
		graph03 = OwnGraph.getInstanceFromFile(file);
		shortestPath = new DijekstraBFS(graph03);
	}

	@After
	public void tearDown() throws Exception {
	}

	//Testet ob der Graph zusammenhängend ist.
	@Test
	public void completeConnectedTest() {
		this.graph03.realNodes().forEach(
				n -> {
					shortestPath.run(n);
					boolean reachable = this.graph03.realNodes()
							.filter(a -> !n.equals(a))
							.allMatch(a -> shortestPath.getShortestPath(a).size() > 0);
					assertTrue(reachable);
					});
	}

	//Testet ob Soltau und Rotenburg dieselbe Kante als kürzesten Pfad nutzen.
	@Test
	public void UseSameUndirectedEdgeTest() {
		OwnNode a = this.graph03.getNode("Soltau");
		OwnNode j = this.graph03.getNode("Rotenburg");

		//a -> j
		shortestPath.run(a);
		Path path1 = shortestPath.getShortestPath(j);

		//j -> a
		shortestPath.run(j);
		Path path2 = shortestPath.getShortestPath(a);

		assertEquals(path1.getEdgePath(), path2.getEdgePath());
	}

	//Testet ob von Husum nach Rotenburg auch Kiel enthalten ist.
	@Test
	public void BetweenNodeTest() {
		OwnNode bh = this.graph03.getNode("Husum");
		OwnNode h = this.graph03.getNode("Rotenburg");

		//(Husum -> Kiel -> Uelzen -> Rotenburg) ist kürzer als
		//(Husum -> Norderstedt -> Bremerhaven -> Rotenburg)
		shortestPath.run(bh);
		Path path = shortestPath.getShortestPath(h);
		long c = path.getNodePath().stream().filter(n -> n.getId().contentEquals("Kiel")).count();
		assertTrue(c == 1);
	}
}