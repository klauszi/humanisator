/**
 * 
 */
package com.human.element;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.human.model.OwnGraph;

/**
 * @author human
 *
 */
public class TestOwnGraph {
	
	public OwnGraph oneNode;

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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    String toWrite = "node1;";
	    File nodeFile = File.createTempFile("onlyNode", ".tmp");
	    FileWriter writer = new FileWriter(nodeFile);
	    writer.write(toWrite);
	    writer.close();
	    oneNode = OwnGraph.getInstanceFromFile(nodeFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.human.model.OwnGraph#getInstance(java.io.File)}.
	 */
	@Test
	public void testGetInstanceFromFile() {
	    int actualCount = oneNode.getNodeCount();
	    int expectedCount = 2;
	    assertEquals(expectedCount, actualCount);
	}

	/**
	 * Test method for {@link com.human.model.OwnGraph#toString()}.
	 */
	@Test
	public void testToString() {
	    String actual = oneNode.toString();
	    String expected = "node1;\n";
	    assertEquals(expected, actual);
	}

}
