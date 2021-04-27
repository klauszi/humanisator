package com.human.main;


import java.io.File;

import javax.swing.SwingUtilities;

import com.human.model.OwnGraph;
import com.human.view.GraphView;


public class App {
	
	public static File exampleGraph() {
		File file = new File("");
		String fileName = "graph11.gka";
		try {
		    ClassLoader classLoader = App.class.getClassLoader();
		    file = new File(classLoader.getResource(fileName).getFile());
		}
		catch (Exception e) {
			System.out.println("graph01.gka cannot found!");
		}
		return file;
	}
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                	File file = exampleGraph();
                	OwnGraph graph = OwnGraph.getInstanceFromFile(file);
                    new GraphView(graph);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}