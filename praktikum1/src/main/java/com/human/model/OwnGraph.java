package com.human.model;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AbstractNode;

public class OwnGraph extends MultiGraph {
	
	private String NODEREG = "^(?<nodeName>[\\w�]+);$";
	private String EDGEREG = "^(?<from>[\\w�]+) "
			+ "(?<edgeType>->||--) "
			+ "(?<to>[\\w�]+)"
			+ "(?<edgeName> \\(([\\w�]+)\\))?"
			+ "( : (?<weight>\\d+))?;$";

	private Pattern nodePattern = Pattern.compile(NODEREG);
	private Pattern edgePattern = Pattern.compile(EDGEREG);
	
	// Falls kein Kantennehmen, dann Id zuweisen.
	static private int edgeId = 0;
	
	// EmptyNode, um Zusammenhängigkeit  zu garantieren
	// wegen Design
	private OwnNode emptyNode;

	public OwnGraph(String id) {
		super(id);

		// Neuen Knotentypen OwnNode erzwingen 
		setNodeFactory(new NodeFactory<OwnNode>() {
			public OwnNode newInstance(String id, Graph graph) {
				return new OwnNode((AbstractGraph) graph, id);
			}
		});
		
		// Neuen Kantentypen OwnEdge erzwingen 
		setEdgeFactory(new EdgeFactory<OwnEdge>() {
			public OwnEdge newInstance(String id, Node src, Node dst, boolean directed) {
				return new OwnEdge(id, (AbstractNode) src, (AbstractNode) dst, directed);
			}
		});

		//Verstecke den leeren Knoten
		emptyNode = (OwnNode) super.addNode("EMPTY");
		emptyNode.setAttribute("ui.hide");
	}
	
	/**
	 * Fügt einen Element aus der line Hinzu
	 * @param line Zeile mit Graphenelement, welches hinzugefügt wird. 
	 * @return Erfolgreiches Hinzufügen
	 */
	public boolean addElementFromLine(String line)
	{
		Matcher nodeMatcher = nodePattern.matcher(line);
		Matcher edgeMatcher = edgePattern.matcher(line);

		if(edgeMatcher.find()){
			//Daten, die man braucht 
			String from, to, edgeName;
			int weight;
			boolean directed;

			//Daten auslesen
			from = edgeMatcher.group("from");
			to = edgeMatcher.group("to");
			edgeName = edgeMatcher.group("edgeName");
			try {
				weight = Integer.valueOf(edgeMatcher.group("weight"));
			}
			catch(NumberFormatException e){
				weight = 1;
			}
			directed = edgeMatcher.group("edgeType").equals("->");

			// Kante definieren
			Edge edge = this.addEdge(from, to, directed, weight);
			if(edgeName == null) {
				edge.setAttribute("ui.label", "(" + edge.getId() + ")" + "W" + weight);
			}
			else {
				edge.setAttribute("ui.label", edgeName.trim() + "W" + weight);
				edge.setAttribute("hasLabel", true);
			}
			return true;
		}
		else if(nodeMatcher.find()) {
			String nodeName = nodeMatcher.group("nodeName");
			this.addNode(nodeName);
			return true;
		}
		else {
			return false;
		}
	}

	// liefert eine neue Instanz durch Angabe einer Datei
	public static OwnGraph getInstanceFromFile(File file) {
		String fileName = file.getName();
		OwnGraph graph = new OwnGraph(fileName);
		try{
			Scanner in = new Scanner(file);
			while(in.hasNext()) {
				String line = in.nextLine();
				boolean succeed = graph.addElementFromLine(line);
				if(!succeed){
					System.out.println("no match: " + line);
				}
			}
			in.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
		return graph;
	}
	
	/**
	 * Fügt, falls nicht vorhanden, einen Knoten hinzu ung gibt
	 * den Knoten mit der Id garantiert zurück. 
	 */
	@Override
	public OwnNode addNode(String id) {
		OwnNode node = getNode(id);
		if(node == null) {
			node = (OwnNode) super.addNode(id);
			Edge edge = addEdge(node.getId(), emptyNode.getId(), false, 0);
			edge.setAttribute("ui.hide");
			}
		else {
			node.setAttribute("ui.label", id);
		}
		return node;
	}
	
	public OwnEdge addEdge(String from, String to, boolean directed, int weight) {
		OwnNode source, target;
		source = this.addNode(from);
		target = this.addNode(to);
		OwnEdge edge = (OwnEdge) super.addEdge(String.valueOf(edgeId), source, target, directed);
		edge.setWeight(weight);
		edgeId += 1;
		return edge;
	}
	
	//nodes() braucht man für Visualisierung. Daher eigenen Stream für Verarbeitung
	public Stream<OwnNode> realNodes(){
		return super.nodes().filter(n -> !n.equals(emptyNode)).map(n -> (OwnNode) n);
	}

	//edges() braucht man für Visualisierung. Daher eigenen Stream für Verarbeitung
	public Stream<OwnEdge> realEdges(){
		return super.edges().filter(e -> !e.getTargetNode().equals(emptyNode)).map(e -> (OwnEdge) e);
	}
	
	//toString entspricht Speicherformat
	@Override
	public String toString() {
		String printnodes = realNodes().map(n -> n.toString() + "\n").collect(Collectors.joining());
		String printedges = realEdges().map(e -> e.toString() + "\n").collect(Collectors.joining());
		return printnodes + printedges;
	}
	
	@Override
	public OwnNode getNode(String id) {
		return (OwnNode) super.getNode(id);
	}
}