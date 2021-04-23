package com.human.algorithm;

import org.graphstream.graph.Path;

import com.human.model.OwnNode;

public interface ShortestPath {
	
	/**
	 * Berechnet für start alle kürzeste Pfade.
	 * @param start Quelle
	 */
	public void run(OwnNode start);

	/**
	 * Liefert den Pfad von gesetzter Quelle zum Zielknoten zurück. 
	 * @param target Zielknoten
	 * @return Pfad start -> ... -> target
	 */
	public Path getShortestPath(OwnNode target);
	
	/**
	 * Liefert die Kosten des kürzesten Pfades von gesetzter Quelle
	 * zum Zielknoten zurück. Die Kosten eines Pfades entspricht
	 * die Summe der Kantengewichte.
	 * @param target
	 * @return
	 */
	public int getShortestPathValue(OwnNode target);

}
