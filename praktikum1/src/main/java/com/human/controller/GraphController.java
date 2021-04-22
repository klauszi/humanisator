package com.human.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.graphstream.graph.Path;

import com.human.algorithm.DijekstraBFS;
import com.human.algorithm.ShortestPath;
import com.human.model.OwnEdge;
import com.human.model.OwnGraph;
import com.human.model.OwnNode;
import com.human.view.GraphView;

public class GraphController implements ActionListener{
	
	private GraphView view;
	private OwnGraph graph;
	private ShortestPath shortestPath;
	
	public GraphController(GraphView view, OwnGraph graph) {
		this.view = view;
		this.graph = graph;
		this.shortestPath = new DijekstraBFS(graph);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Component clickedComponent = (Component) arg0.getSource();
		System.out.println(clickedComponent);
		if(clickedComponent == view.getSaveItem()) {
			save();
		}
		else if (clickedComponent == view.getLoadItem()) {
			load();
		}
		else if (clickedComponent == view.getStart()) {
			start();
		}
		else if (clickedComponent == view.getTarget()) {
			target();
		}
		else if (clickedComponent == view.getSource()) {
			source();
		}
		else {
			System.out.println("error event");
		}
	}
	
	public void start() {
		OwnNode source = (OwnNode) view.getSourceNode();
		OwnNode target = (OwnNode) view.getTargetNode();
		shortestPath.run(source);
		Path path = shortestPath.getShortestPath(target);
		int numberOfEdges = path.getEdgeCount();
		String numberOfEdgesPrint;
		if(numberOfEdges == 0 && target != source) {
			numberOfEdgesPrint = "unreachable";
		}
		else {
			numberOfEdgesPrint = String.valueOf(numberOfEdges);
		}
		view.getNumberEdges().setText(numberOfEdgesPrint);
		view.colorPath(path);
	}

	public void target() {
		String targetId = (String) view.getTarget().getSelectedItem();
		view.uncolorEdges();
		view.setTargetNode(targetId);
	}

	public void source() {
		String sourceId = (String) view.getSource().getSelectedItem();
		view.uncolorEdges();
		view.setSourceNode(sourceId);
	}
	
	public void save() {
            JFileChooser chooser = new JFileChooser();
            int userSelection = chooser.showSaveDialog(view);
            
            // wir wollen nur GKA Dokumente
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileFilter() {
                public String getDescription() {
                    return "GKA Documents (*.gka)";
                }

                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        return f.getName().toLowerCase().endsWith(".gka");
                    }
                }
            });
            
            // Schreib die Auswahl raus
            if(userSelection == JFileChooser.APPROVE_OPTION) {
            	File file = chooser.getSelectedFile();
            	FileWriter writer;
				try {
					writer = new FileWriter(file);
					writer.write(graph.toString());
					writer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
	}
	
	public void load() {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileFilter() {
                public String getDescription() {
                    return "GKA Documents (*.gka)";
                }
             
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        return f.getName().toLowerCase().endsWith(".gka");
                    }
                }
            });
            int chooserValue = chooser.showOpenDialog(null);
            if(chooserValue == JFileChooser.APPROVE_OPTION) {
            	File file = chooser.getSelectedFile();
            	OwnGraph newGraph = OwnGraph.getInstanceFromFile(file);
            	view.updateGraph(newGraph);
            	this.graph = newGraph;
            	this.shortestPath = new DijekstraBFS(this.graph);
            }
	}

}
