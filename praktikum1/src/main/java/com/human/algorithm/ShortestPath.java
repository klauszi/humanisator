package com.human.algorithm;

import org.graphstream.graph.Path;

import com.human.model.OwnNode;

public interface ShortestPath {
	
	public Path getShortestPath(OwnNode target);
	public int getShortestPathValue(OwnNode target);
	public void run(OwnNode start);

}
