package gui;

import graphs.*;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//XML Parsing
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLLoadHandler implements EventHandler<ActionEvent> {
	private Stage stage;
	private Graph graph;

	public XMLLoadHandler(Stage stage, Graph graph) {
		this.stage = stage;
		this.graph = graph;
	}

	@Override
	public void handle(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open graph xml file");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML-File", "*.xml"));
		File defaultDirectory = new File("C:/Users/Dennis/workspace/ALDA-AStar/graphs");
		fileChooser.setInitialDirectory(defaultDirectory);

		File file = fileChooser.showOpenDialog(stage);

		try {
			parseXML(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to read XML.");
			e.printStackTrace();
		}
	}

	private void parseXML(File file) throws Exception {
		// clear (old) graph
		graph.clear();

		// loading DOM in RAM
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();

		NodeList nodeList;

		// selecting the graph-nodes (NOT xml-nodes)
		nodeList = doc.getElementsByTagName("node");

		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node xmlNode = nodeList.item(i);
			if (xmlNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				Element xmlElement = (Element) xmlNode;
				// single graph-node now in xmlElement

				String nodeName = xmlElement.getElementsByTagName("name").item(0).getTextContent();
				int nodePosX = Integer.parseInt(xmlElement.getElementsByTagName("posX").item(0).getTextContent());
				int nodePosY = Integer.parseInt(xmlElement.getElementsByTagName("posY").item(0).getTextContent());

				Node node = new Node(nodeName, nodePosX, nodePosY);
				graph.addNode(node);
			}
		}

		// selecting the graph-edges
		nodeList = doc.getElementsByTagName("edge");

		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node xmlNode = nodeList.item(i);
			if (xmlNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				Element xmlElement = (Element) xmlNode;
				// single graph-node now in xmlElement

				Node nodeFrom = graph.getNodeByName(xmlElement.getElementsByTagName("from").item(0).getTextContent());
				Node nodeTo = graph.getNodeByName(xmlElement.getElementsByTagName("to").item(0).getTextContent());
				double cost = Double.parseDouble(xmlElement.getElementsByTagName("cost").item(0).getTextContent());

				Edge edge = new Edge(nodeFrom, nodeTo, cost);
				graph.addEdge(edge);
			}
		}
		System.out.println("DEBUG: Graph sysout:");
		System.out.println(graph.toString());

		MainWindow.renderGraph();
	}
}
