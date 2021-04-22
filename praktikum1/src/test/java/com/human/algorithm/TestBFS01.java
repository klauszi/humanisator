package com.human.algorithm;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.human.main.App;
import com.human.model.OwnEdge;
import com.human.model.OwnGraph;
import com.human.model.OwnNode;

public class TestBFS01 {
	OwnGraph graph01;
	BFS bfs;
	final String SHOWN = "shown";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// Lädt Graph01 hoch
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

		//Anonymes Objekt. Setzt für jeden Kante die Anzahl der Benutzungen.
		bfs = new BFS(graph01) {
			@Override
			public void visit(OwnNode source, OwnEdge usedEdge) {
				if(usedEdge.hasAttribute(SHOWN)){
					int shownNumber = (int) usedEdge.getAttribute(SHOWN);
					usedEdge.setAttribute(SHOWN, shownNumber + 1);
				}
				else {
					usedEdge.setAttribute(SHOWN, 1);
				}
			}
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	// Überprüft, ob alle Kanten besucht wurden. Alle Knoten mit Kanten sind von i erreichbar.
	@Test
	public void testAllEdgesVisited() {
		OwnNode a = graph01.getNode("i");
		bfs.run(a);
		boolean allShown = graph01.realEdges()
				.allMatch(e -> e.hasAttribute(SHOWN));
		assertTrue(allShown);
	}

	// Überprüft, ob gerichtete Kanten genau einmal besucht wurden
	@Test
	public void testAllDirectedEdgesOnlyOneVisited() {
		OwnNode a = graph01.getNode("i");
		bfs.run(a);
		boolean allShown = graph01.realEdges()
				.filter(e -> e.isDirected())
				.map(e -> (int) e.getAttribute(SHOWN))
				.allMatch(number -> number == 1);
		assertTrue(allShown);
	}
}
