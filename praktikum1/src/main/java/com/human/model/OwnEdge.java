package com.human.model;

import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;

public class OwnEdge extends AbstractEdge implements Edge, IOwnEdge{
	private int DEFAULTWEIGHT = 1;
	
	public OwnEdge(String id, AbstractNode src, AbstractNode dst, boolean directed) {
		super(id, src, dst, directed);
		this.setAttribute("weight", DEFAULTWEIGHT);
		this.setAttribute("hasLabel", false);
	}
	
	@Override
	public String toString() {
		
		// Alle Metadaten zum Spezifieren einer Kante 
		int weight = (int) this.getAttribute("weight");
		String label = (String) this.getAttribute("ui.label");
		String sourceName = this.getSourceNode().getId();
		String targetName = this.getTargetNode().getId();
		String edgeType = this.directed ? " -> ": " -- ";
		boolean hasLabel = (boolean) this.getAttribute("hasLabel");
		boolean hasWeight = !(DEFAULTWEIGHT == weight);
		
		// TODO: Vereinfachen
		if(hasLabel && hasWeight){
			return sourceName + edgeType + targetName + "( " + label + ")" + " : " + weight + ";";
		}
		else if(hasLabel) {
			return sourceName + edgeType + targetName + "( " + label + ")" + ";";
		}
		else if(hasWeight) {
			return sourceName + edgeType + targetName + " : " + weight + ";";
		}
		else {
			return sourceName + edgeType + targetName;
		}
	}
	
	public OwnNode getNode0() {
		return (OwnNode) super.getNode0();
	}

	public OwnNode getNode1() {
		return (OwnNode) super.getNode1();
	}

	public OwnNode getAnotherNode(OwnNode node) {
		OwnNode n1 = this.getNode1();
		OwnNode n2 = this.getNode0();
		if(node.equals(n2)) {
			return n1;
		}
		else {
			return n2;
		}
	}
}
