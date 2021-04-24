package com.human.model;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
	
	static String NODEREG = "^([\\w�]+);$";
	static String EDGEREG = "^([\\w�]+) (->||--) ([\\w�]+)( \\(([\\w�]+)\\))?( : (\\d+))?;$";

	static Pattern nodePattern = Pattern.compile(NODEREG);
	static Pattern edgePattern = Pattern.compile(EDGEREG);
	
	// Gibt für ein Attribut die entsprechende GroupId der Pattern zum Nachschlagen zurück.  
	static Map<String, Integer> ATTR2GROUP = Stream.of(new Object[][] {
		{"from", 1},
		{"edgeType", 2},
		{"to", 3},
		{"edgeName", 5},
		{"weight", 7},
	}).collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));
	
	static private int edgeId = 0;
	
	// EmptyNode, um Zusammenhängigkeit  zu garantieren
	// wegen Design
	private OwnNode emptyNode;

	// Instanziierung mithilfe vom Konstruktor wird vermieden,
	// um ungünstige Überladung zu vermeiden.
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

		//Verstecke den leeren Knoten
		emptyNode = (OwnNode) super.addNode("EMPTY");
		emptyNode.setAttribute("ui.hide");
	}
	

	/**
	 * Liest eine Zeile. Liefert ein Map zurück, der für jedes Attribut den
	 * entsprechend gelesenen Wert angibt. Falls die Zeile nicht zum Pattern passt,
	 * wird ein leeres Map zurückgegeben.
	 * @param line
	 * @return Attribute
	 */
	static private Map<String, String> readValuesFromLine(String line){
		Matcher nodeMatcher = nodePattern.matcher(line);
		Matcher edgeMatcher = edgePattern.matcher(line);
		Map<String, String>	attr =  new HashMap<String, String>();

		if(edgeMatcher.find()) {
			attr = ATTR2GROUP.keySet()
					.stream()
					.collect(Collectors.toMap(
						key -> key,
						key -> (String) Optional.ofNullable(edgeMatcher.group(ATTR2GROUP.get(key))).orElse("null")));
		}
		else if(nodeMatcher.find()) {
			String key = "from";
			String nodeName = nodeMatcher.group(ATTR2GROUP.get(key));
			attr.put(key, nodeName);
		}
		return attr;
	}
	
	/**
	 * Fügt einen Element abhängig von den gesetzten Attributen und Werten hinzu
	 * @param graph
	 * @param attrToValue Attribute und zugehörige Werte
	 * @return
	 */
	static private boolean addElement(OwnGraph graph, Map<String, String> attrToValue)
	{
		//gültige Keys
		Set<String> EdgeKeys = ATTR2GROUP.keySet();
		Set<String> NodeKeys = new HashSet<String>(Arrays.asList("from"));
		
		//Überprüfung auf Gültigkeit
		boolean validEdgeKeys = attrToValue.keySet().equals(ATTR2GROUP.keySet());
		boolean validNodeKeys = attrToValue.keySet().equals(NodeKeys);
		
		
		if(validEdgeKeys){
			//Daten aus der Map auslesen
			String from = attrToValue.get("from");
			String edgeType = attrToValue.get("edgeType");
			String to = attrToValue.get("to");
			String edgeName = attrToValue.get("edgeName");
			String weight = attrToValue.get("weight");

			int weightValue = weight == "null"? 1 : Integer.valueOf(weight);
			boolean directed = edgeType.equals("->");

			Edge edge = graph.addEdge(from, to, directed, weightValue);
			if(edgeName == "null") {
				edge.setAttribute("ui.label", "(" + edge.getId() + ")" + "W" + weightValue);
			}
			else {
				edge.setAttribute("ui.label", edgeName.trim() + "W" + weightValue);
				edge.setAttribute("hasLabel", true);
			}
			return true;
		}
		else if(validNodeKeys) {
			String nodeName = attrToValue.get("from");
			graph.addNode(nodeName);
			return true;
		}
		else {
			return false;
		}
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
				Map<String, String> attrToValues = readValuesFromLine(line);
				boolean succeed = addElement(graph, attrToValues);
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
		OwnEdge edge = (OwnEdge) addEdge(String.valueOf(edgeId), source, target, directed);
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