package com.human.algorithm;

import org.graphstream.graph.Path;

import com.human.model.OwnEdge;
import com.human.model.OwnGraph;
import com.human.model.OwnNode;

public class DijekstraBFS extends BFS implements ShortestPath{
	
	private final int INF = Integer.MAX_VALUE;
	private final OwnNode NOPRE = null;
	private final String PREEDGE = "pre";
	private final String DIST = "dist";
	private OwnNode start;

	public DijekstraBFS(OwnGraph graph) {
		super(graph);
		start = null;
	}

	@Override
	protected void visit(OwnNode source, OwnEdge usedEdge) {
		OwnNode visitedNode = usedEdge.getAnotherNode(source);

		int oldValue = (int) visitedNode.getAttribute(DIST);
		int preValue = (int) source.getAttribute(DIST);
		int weight = (int) usedEdge.getAttribute("weight");
		
		// Relaxieren
		if(oldValue > preValue + weight) {
			visitedNode.setAttribute(DIST, preValue + weight);
			visitedNode.setAttribute(PREEDGE, usedEdge);
		}
	}
	
	@Override
	public void run(OwnNode start) {
		graph.realNodes()
			.peek(n -> n.setAttribute(PREEDGE, NOPRE))
			.forEach(n -> n.setAttribute(DIST, INF));
		this.start = start;
		this.start.setAttribute(DIST, 0);
		super.run(this.start);
	}
	
	public Path getShortestPath(OwnNode target) {
		Path path = new Path();
		path.setRoot(target);
		if(target.getAttribute(PREEDGE) == null) return path;
		OwnNode current = target;
		while(!current.equals(start)) {
			OwnEdge preEdge = (OwnEdge) current.getAttribute(PREEDGE);
			OwnNode preNode;
			if(preEdge.getNode0().equals(current)){
				preNode	= preEdge.getNode1();
			}
			else {
				preNode = preEdge.getNode0();
			}
			path.add(preEdge);
			current = preNode;
		}
		return path;
	}

	public int getShortestPathValue(OwnNode target) {
		return (int) target.getAttribute(DIST);
	}
}