/** Breitensuche
 * 
 */
package com.human.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.human.model.OwnEdge;
import com.human.model.OwnGraph;
import com.human.model.OwnNode;

/**
 * @author human
 *
 */
public abstract class BFS {
	protected OwnGraph graph;
	
	public BFS(OwnGraph graph) {
		this.graph = graph;
	}
	
	// Was bei einem Besuch getan werden sollte
	abstract protected void visit(OwnNode source, OwnEdge usedEdge);

	public void run(OwnNode start) {
		// Merken schon besuchter Knoten in einer Menge (Knoten sind eindeutig!)
		Set<OwnNode> visitedNodes = new HashSet<OwnNode>();
		// Merken von Knoten, die nach BFS noch besucht werden müssen
		Queue<OwnNode> toVisitNodes = new LinkedList<OwnNode>();
		// Füge den Startknoten zu den noch besuchten Knoten hinzu
		toVisitNodes.add(start);
		
		while(!toVisitNodes.isEmpty())
		{
			// Man hole den ersten hinzugefügten Knoten
			OwnNode current = toVisitNodes.poll();
			if(!visitedNodes.contains(current)) {
				// Man schaue seine Kanten an
				current.realEdges()
					// und besucht seine Nachbarn (kann auch schon als besucht markiert sein,
					// weil andere oder ungerichtete Kante vorliegt),
					.peek(e -> visit(current, e))
					// jetzt wollen wir den Nachbarn genauer anschauen
					.map(e -> e.getOpposite(current))
					// Falls der Nachbar noch nie besucht worden ist,
					.filter(n -> !visitedNodes.contains(n))
					// dann müssen wir ihn in Zukunft besuchen! 
					.forEach(unvisitedNode -> toVisitNodes.add((OwnNode) unvisitedNode));
				// Jetzt markieren wir den aktuellen Knoten als besucht! 
				visitedNodes.add(current);
			}
		}
	}
}
