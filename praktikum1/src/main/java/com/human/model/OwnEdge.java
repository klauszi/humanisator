package com.human.model;

import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;

public class OwnEdge extends AbstractEdge implements Edge{
	private int DEFAULTWEIGHT = 1;
	private int weight;
	
	public OwnEdge(String id, AbstractNode src, AbstractNode dst, boolean directed) {
		super(id, src, dst, directed);
		this.weight = DEFAULTWEIGHT;
	}
	
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		
		// Alle Metadaten zum Spezifieren einer Kante 
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
}
