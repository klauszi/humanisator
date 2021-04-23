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

public class TestDijekstra02 {
	OwnGraph graph02;
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
		String fileName = "graph02.gka";
		try {
		    ClassLoader classLoader = App.class.getClassLoader();
		    file = new File(classLoader.getResource(fileName).getFile());
		}
		catch (Exception e) {
			System.out.println("graph01.gka cannot found!");
		}
		graph02 = OwnGraph.getInstanceFromFile(file);
		shortestPath = new DijekstraBFS(graph02);
	}

	@After
	public void tearDown() throws Exception {
	}

	//Testet ob der Graph zusammenhängend ist.
	@Test
	public void completeConnectedTest() {
		this.graph02.realNodes().forEach(
				n -> {
					shortestPath.run(n);
					boolean reachable = this.graph02.realNodes()
							.filter(a -> !n.equals(a))
							.allMatch(a -> shortestPath.getShortestPath(a).size() > 0);
					assertTrue(reachable);
					});
	}

	//Testet ob a und j dieselbe Kante als kürzesten Pfad nutzen.
	@Test
	public void UseSameUndirectedEdgeTest() {
		OwnNode a = this.graph02.getNode("a");
		OwnNode j = this.graph02.getNode("j");

		//a -> j
		shortestPath.run(a);
		Path path1 = shortestPath.getShortestPath(j);

		//j -> a
		shortestPath.run(j);
		Path path2 = shortestPath.getShortestPath(a);

		assertEquals(path1.getEdgePath(), path2.getEdgePath());
	}

	// Da alle Kanten ungerichtet sind und der Graph zusammenhängend ist,
	// ist der kürzeste Pfad von a -> b genauso lang wie von b -> a.
	// Da es mehrere kürzeste Pfade gibt, wird es hier anhand der Länge bestimmt.
	@Test
	public void UseUndirectedEdgeTest() {
		this.graph02.realNodes().forEach(
				n -> {
					this.graph02.realNodes()
							.filter(a -> !n.equals(a))
							.forEach(a ->{
									shortestPath.run(n);
									Path path1 = shortestPath.getShortestPath(a);
									shortestPath.run(a);
									Path path2 = shortestPath.getShortestPath(n);
									assertEquals(path1.getEdgeCount(), path2.getEdgeCount());
							});
					});
	}
}