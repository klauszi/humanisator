package com.human.view;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.graphstream.graph.Path;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import com.human.controller.GraphController;
import com.human.model.OwnEdge;
import com.human.model.OwnGraph;
import com.human.model.OwnNode;

public class GraphView extends JFrame{
	private final String GRAPHSTYLE = "graph.css";
	private JMenuItem loadItem, saveItem;
	private Button start;
	private Label numberEdges, fileName;
	private JPanel gmod, alg, panel;
	private JMenuBar menuBar;
	private OwnGraph graph;
	private View view;
	private JComboBox source, target;
	private OwnNode targetNode, sourceNode;

	public void setFileName(String name) {
		fileName.setText(name);
	}


	public Label getNumberEdges() {
		return numberEdges;
	}

	public void setNumberEdges(Label numberEdge) {
		this.numberEdges = numberEdge;
	}

	
	// ist nur dafür da, damit die Warnung verschwindet.
	private final static long serialVersionUID = 1L;
	
	private void setGraphStyle(){
		try {
		    ClassLoader classLoader = getClass().getClassLoader();
		    File file = new File(classLoader.getResource(GRAPHSTYLE).getFile());
		    String content = new String(Files.readAllBytes(file.toPath()));
		    graph.setAttribute("ui.stylesheet", content);
		}
		catch (Exception e) {
			System.out.println("graph style failed!");
		}
	}
	
	public GraphView(OwnGraph graph) {
		super("Humanisator");
        this.setMinimumSize(new Dimension(1000, 500));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
        this.graph = graph;
        
        initMenu();
        initAlg();
        initGraph();
        
        GraphController controller = new GraphController(this, this.graph);
        submitToController(controller);
        
        fileName = new Label(this.graph.getId());
        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(fileName, BorderLayout.NORTH);
        //panel.add(gmod, BorderLayout.WEST);
        panel.add(alg, BorderLayout.EAST);
        this.add(panel);
        
        updateGraph(graph);
	}
	
	private void submitToController(ActionListener controller) {
		AbstractButton[] subscribers = {loadItem, saveItem};
		Arrays.stream(subscribers)
			.forEach(subscriber -> subscriber.addActionListener(controller));
		start.addActionListener(controller);
		source.addActionListener(controller);
		target.addActionListener(controller);
	}
	
	public void setTargetNode(String targetId) {
		if(targetId != null) {
			if(this.targetNode == null) {
				targetNode = (OwnNode) graph.getNode(targetId);
			}
			else {
				targetNode.setAttribute("ui.class", "normal");
			}
			targetNode = (OwnNode) this.graph.getNode(targetId);
			targetNode.setAttribute("ui.class", "target");
		}
	}
	
	public void colorPath(Path path) {
		uncolorEdges();
		this.graph.realEdges()
			.filter(path::contains)
			.forEach(e -> e.setAttribute("ui.color", Color.RED));
	}

	public void uncolorEdges() {
		this.graph.realEdges()
			.forEach(e -> e.removeAttribute("ui.color"));
	}

	public void setSourceNode(String sourceId) {
		if(sourceId != null) {
			if(this.sourceNode == null) {
				sourceNode = (OwnNode) graph.getNode(sourceId);
			}
			else {
				sourceNode.setAttribute("ui.class", "normal");
			}
			sourceNode = (OwnNode) this.graph.getNode(sourceId);
			sourceNode.setAttribute("ui.class", "source");
		}
	}
	
	public Button getStart() {
		return start;
	}

	public void setStart(Button start) {
		this.start = start;
	}

	public JComboBox getSource() {
		return source;
	}

	public void setSource(JComboBox source) {
		this.source = source;
	}

	public JComboBox getTarget() {
		return target;
	}

	public void setTarget(JComboBox target) {
		this.target = target;
	}

	public void updateGraph(OwnGraph graph) {
		if(view != null) panel.remove((Component) view);
		this.graph = graph;
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		setGraphStyle();
		this.graph.realNodes().forEach(n -> n.setAttribute("ui.class", "normal"));
		SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		view = viewer.addDefaultView(false);
        panel.add((Component) view, BorderLayout.CENTER);
		viewer.enableAutoLayout();
        this.setJMenuBar(menuBar);
        this.setVisible(true);
        OptionalInt maxWeight = this.graph.edges().mapToInt(e -> (int) e.getAttribute("weight")).max();
        this.graph.edges()
        	.forEach(e -> {double weight = Double.valueOf((int) e.getAttribute("weight"));
        				  double thick = 10 * (weight / maxWeight.orElse(1));
        				  e.setAttribute("ui.size", String.valueOf(thick) + "px");});
        
        String[] nodeNames = this.graph.realNodes().map(n -> (String) n.getAttribute("ui.label")).toArray(String[]::new);;
        DefaultComboBoxModel<String> sourceModel = (DefaultComboBoxModel<String>) source.getModel();
        sourceModel.removeAllElements();
        sourceModel.addAll(Arrays.asList(nodeNames));

        DefaultComboBoxModel<String> targetModel = (DefaultComboBoxModel<String>) target.getModel();
        targetModel.removeAllElements();
        targetModel.addAll(Arrays.asList(nodeNames));
	}
	
	@SuppressWarnings("unchecked")
	private void initAlg() {
        alg = new JPanel();
        JPanel alg1 = new JPanel();
        JPanel alg2 = new JPanel();

        alg.setLayout(new BoxLayout(alg, BoxLayout.PAGE_AXIS));
        JLabel title = new JLabel ("Dijkstra", SwingConstants.LEFT);
        title.setFont (title.getFont ().deriveFont (32.0f));
        alg1.add(title);

        String[] nodeNames = this.graph.realNodes().map(n -> (String) n.getAttribute("ui.label")).toArray(String[]::new);;

        Label sourceLabel = new Label("Source");
        sourceLabel.setBackground(Color.RED);
        alg1.add(sourceLabel);
        source = new JComboBox();
        alg1.add(source);

        Label targetLabel = new Label("Target");
        targetLabel.setBackground(Color.GREEN);
        alg1.add(targetLabel);
        target = new JComboBox();
        alg1.add(target);

        alg2.add(new Label("Number of Edges"));
        numberEdges = new Label("");
        numberEdges.setPreferredSize(new Dimension(100, 100));
        alg2.add(numberEdges);
        start = new Button("start");
        alg2.add(start);
        alg.add(alg1);
        alg.add(alg2);
		
	}
	
	public OwnNode getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(OwnNode targetNode) {
		this.targetNode = targetNode;
	}

	public OwnNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(OwnNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	private void initGraph() {
		gmod = new JPanel();
        gmod.setLayout(new BoxLayout(gmod, BoxLayout.PAGE_AXIS));
        gmod.add(new Label("Graph"));
        gmod.add(new Button("new Node"));
        gmod.add(new Button("new Edge"));
        gmod.add(new Button("remove Node"));
        gmod.add(new Button("remove Edge"));
	}
	
	private void initMenu() {
		menuBar = new JMenuBar();
		
		saveItem = new JMenuItem("Save", KeyEvent.VK_T);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

		loadItem = new JMenuItem("Load", KeyEvent.VK_T);
		loadItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));

		//Build the first menu.
		JMenu menu = new JMenu("File");
		menu.add(saveItem);
		menu.add(loadItem);
		menuBar.add(menu);
	}

	public JMenuItem getLoadItem() {
		return loadItem;
	}

	public JMenuItem getSaveItem() {
		return saveItem;
	}

}
