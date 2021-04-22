/**
 * 
 */
package com.human.model;

import java.util.stream.Stream;


import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListNode;
import org.graphstream.graph.implementations.MultiNode;

/**
 * @author human
 * Eigene Knotenklasse, um z.B. das Format zum Speichern festzulegen
 *
 */
public class OwnNode extends MultiNode implements Node{
	
	public OwnNode(AbstractGraph graph, String id) {
		super(graph, id);
	}
	
	public Stream<OwnEdge> realEdges(){
		return super.edges().map(e -> (OwnEdge) e).filter(e -> !ishidden(e));
	}

	private boolean ishidden(Edge e) {
		boolean hideattribute = e.getTargetNode().hasAttribute("ui.hide");
		if(hideattribute) {
			return (boolean) e.getTargetNode().getAttribute("ui.hide");
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.getId() + ";";
	}
}