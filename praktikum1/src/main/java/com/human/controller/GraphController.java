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
import com.human.view.GKAChooser;
import com.human.view.GraphView;

/**
 * @author human
 *
 */
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
	public void actionPerformed(ActionEvent event) {
		Component clickedComponent = (Component) event.getSource();
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
			this.updateSSSP();
		}
		else if (clickedComponent == view.getSource()) {
			this.updateSSSP();
		}
		else {
			System.out.println("error event");
		}
	}
	
	/**
	 * Berechnet den kürzesten Pfad, färbt den Pfad und setzt
	 * die Anzahl der genutzten Pfade
	 */
	public void start() {
		// kürzesten Pfad von Quelle zum Zielknoten bestimmen
		OwnNode source = (OwnNode) view.getSourceNode();
		OwnNode target = (OwnNode) view.getTargetNode();
		shortestPath.run(source);
		Path path = shortestPath.getShortestPath(target);
		
		//Anzahl der Kanten setzen
		int numberOfEdges = path.getEdgeCount();
		String numberOfEdgesPrint;
		if(numberOfEdges == 0 && target != source) {
			numberOfEdgesPrint = "unreachable";
		}
		else {
			numberOfEdgesPrint = String.valueOf(numberOfEdges);
		}
		view.getNumberEdges().setText(numberOfEdgesPrint);

		//Pfad färben
		view.colorPath(path);
	}

	/**
	 * Aktualisert den Zielknoten  und färbt diesen entsprechend.
	 */
	public void updateSSSP() {
		String sourceId = (String) view.getSource().getSelectedItem();
		String targetId = (String) view.getTarget().getSelectedItem();
		view.uncolorEdges();
		view.setTargetNode(targetId);
		view.setSourceNode(sourceId);
	}
	
	/**
	 *  Öffnet eine Dateiauswahlfenster und speichert
	 *  den aktuellen Graphen in die Auswahl.
	 */
	public void save() {
		JFileChooser chooser = new GKAChooser();
        int userSelection = chooser.showSaveDialog(null);
        if(userSelection == JFileChooser.APPROVE_OPTION) {
           	File file = chooser.getSelectedFile();
           	FileWriter writer;
			try {
				writer = new FileWriter(file);
				writer.write(graph.toString());
				writer.close();
            	view.setFileName(file.getName());
				}
			catch (IOException e1) {
				e1.printStackTrace();
			}
        }
	}
	
	/**
	 *  Öffnet eine Dateiauswahlfenster und ladet 
	 *  den neuen Graphen in die View.
	 */
	public void load() {
			JFileChooser chooser = new GKAChooser();
            int chooserValue = chooser.showOpenDialog(null);
            if(chooserValue == JFileChooser.APPROVE_OPTION) {
            	File file = chooser.getSelectedFile();
            	OwnGraph newGraph = OwnGraph.getInstanceFromFile(file);
            	view.updateGraph(newGraph);
            	view.setFileName(file.getName());
            	this.graph = newGraph;
            	this.shortestPath = new DijekstraBFS(this.graph);
            }
	}

}
