package com.human.algorithm;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.human.model.OwnGraph;
import com.human.model.OwnNode;

public class TestDijekstraBFS {
	
	OwnGraph simpleGraph;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	    String toWrite = "n1 -> n2;\n"
	    		+ "n1 -- n2 : 7;\n"
	    		+ "n1 -- n3 : 2;\n"
	    		+ "n1 -- n2 (a) : 3;\n"
	    		+ "n2 -> n3 : 4;";
	    File nodeFile = File.createTempFile("onlyNode", ".tmp");
	    FileWriter writer = new FileWriter(nodeFile);
	    writer.write(toWrite);
	    writer.close();
	    simpleGraph = OwnGraph.getInstanceFromFile(nodeFile);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		DijekstraBFS SSSP = new DijekstraBFS(simpleGraph);
		OwnNode start = (OwnNode) simpleGraph.getNode("n1");
		OwnNode target = (OwnNode) simpleGraph.getNode("n3");
		SSSP.run(start);
		SSSP.getShortestPath(target).getEdgePath().stream().forEach(System.out::println);
		System.out.println(SSSP.getShortestPathValue(target));
	}

}
