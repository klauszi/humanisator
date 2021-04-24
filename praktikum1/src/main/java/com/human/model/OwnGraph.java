package com.human.model;

import java.io.File;
import java.util.Scanner;
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
	
	static String NODEREG = "^([\\w�]+);$";
	static String EDGEREG = "^([\\w�]+) (->||--) ([\\w�]+)( \\(([\\w�]+)\\))?( : (\\d+))?;$";

	static Pattern nodePattern = Pattern.compile(NODEREG);
	static Pattern edgePattern = Pattern.compile(EDGEREG);
	
	static private int edgeId = 0;
	
	// EmptyNode, um Zusammenhängigkeit  zu garantieren
	// wegen Design
	private OwnNode emptyNode;

	// Instanziierung mithilfe vom Konstruktor wird vermieden,
	// um ungünstige Überladdung zu vermeiden.
	private OwnGraph(String id) {
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

		emptyNode = (OwnNode) super.addNode("EMPTY");
		emptyNode.setAttribute("ui.hide");
	}
	// TODO auslagen. z.B. durch read(line); 
	// liefert eine neue Instanz durch Angabe einer Datei
	public static OwnGraph getInstanceFromFile(File file) {
		String fileName = file.getName();
		OwnGraph graph = new OwnGraph(fileName);
		try{
			Scanner in = new Scanner(file);
			while(in.hasNext()) {
				String line = in.nextLine();
				Matcher nodeMatcher = nodePattern.matcher(line);
				Matcher edgeMatcher = edgePattern.matcher(line);
				if(edgeMatcher.find()) {
					String from = edgeMatcher.group(1);
					String edgeType = edgeMatcher.group(2);
					String to = edgeMatcher.group(3);
					String edgeName = edgeMatcher.group(5);
					String weight = edgeMatcher.group(7);
					int weightValue;
					if(weight != null) {
						weightValue = Integer.valueOf(weight);
					}
					else {
						weightValue = 1;
					}
					Edge edge;
					if (edgeType.equals("->")) {
						edge = graph.addDirectedEdge(from, to, weightValue);
					}
					else {
						edge = graph.addUndirectedEdge(from, to, weightValue);
					}
					if(edgeName != null) {
						edge.setAttribute("ui.label", edgeName.trim() + "W" + weightValue);
						edge.setAttribute("hasLabel", true);
					}
					else {
						edge.setAttribute("ui.label", "(" + edge.getId() + ")" + "W" + weightValue);
					}

				}
				else if(nodeMatcher.find())
				{
					String nodeName = nodeMatcher.group(1);
					graph.addNode(nodeName);
				}
				else {
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

			// TODO: In die GUI auslagern, weil eher Design
			// Erzeugt eine versteckte Kante, damit der Graph zusammenhängt bleibt,
			// weil Autolayout ansonsten die Knoten ungüngstig verteilt.
			Edge edge = addUndirectedEdge(node.getId(), emptyNode.getId(), 0);
			edge.setAttribute("ui.hide");
			}
		// TODO: In die GUI auslagern, weil eher Design
		node.setAttribute("ui.label", id);
		return node;
	}
	
	public OwnEdge addDirectedEdge(String from, String to, int weight) {
		OwnNode source, target;
		source = this.addNode(from);
		target = this.addNode(to);
		OwnEdge edge = (OwnEdge) addEdge(String.valueOf(edgeId), source, target, true);
		edge.setAttribute("weight", weight);
		edgeId += 1;
		return edge;
	}

	public OwnEdge addUndirectedEdge(String from, String to, int weight) {
		OwnNode source, target;
		source = this.addNode(from);
		target = this.addNode(to);
		OwnEdge edge = (OwnEdge) addEdge(String.valueOf(edgeId), source, target, false);
		edge.setAttribute("weight", weight);
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
	//TODO: implementieren
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