package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import graphs.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainWindow extends Application {
	static final Color colorHTWG = Color.color(0.21176470588, 0.65490196078, 0.91764705882);
	static final int windowSizeX = 500;
	static final int windowSizeY = 800;
	static final int canvasSizeX = 500;
	static final int canvasSizeY = 500;

	static Graph graph = new Graph();
	static GraphicsContext gc;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// XML loading
		XMLLoadHandler xmlLoadHandler = new XMLLoadHandler(primaryStage, graph);
		Button btnLoadXML = new Button();
		btnLoadXML.setText("Load XML");
		btnLoadXML.setOnAction(xmlLoadHandler);

		// Graph displaying canvas
		Canvas canvas = new Canvas(canvasSizeX, canvasSizeY);
		gc = canvas.getGraphicsContext2D();

		// Perform A*
		Button btnPerform = new Button();
		btnPerform.setText("Perform A* Pathfinding");
		btnPerform.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String startNodeName = "";
				String targetNodeName = "";
				List<String> choices = new ArrayList<>();
				for (Node node : graph.getNodes()) {
					choices.add(node.getName());
				}

				ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
				dialog.setTitle("Choose start node");
				dialog.setHeaderText("Please choose the node you want to begin with.");
				dialog.setContentText("Initial node:");
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					System.out.println("Start node: " + result.get());
					startNodeName = result.get();
				}

				dialog.setTitle("Choose target node");
				dialog.setHeaderText("Please choose the node you want to reach.");
				dialog.setContentText("Target node:");
				result = dialog.showAndWait();
				if (result.isPresent()) {
					System.out.println("Target node: " + result.get());
					targetNodeName = result.get();
				}

				try {
					graph.performAStar(startNodeName, targetNodeName);

					// walking the path and grabbing nodes names
					// TODO revert (fill a stack, and then read it again)
					String path = "";
					Node currentNode = graph.getNodeByName(targetNodeName);
					while (currentNode.getPreviousNode() != null) {
						if (path != "") {
							path = path + " --> ";
						}
						path = path + currentNode.getName();
						currentNode = currentNode.getPreviousNode();
					}

					// displaying result
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Path found!");
					alert.setHeaderText("A valid path between " + startNodeName + " and " + targetNodeName
							+ " has been found!");
					alert.setContentText(path);

					alert.showAndWait();

				} catch (NoPathExistsException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("No path");
					alert.setHeaderText(null);
					alert.setContentText("There is no valid path between " + startNodeName + " and " + targetNodeName);

					alert.showAndWait();
				}

			}
		});

		// Putting-together the GUI
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(25, 25, 25, 25));

		pane.add(btnLoadXML, 0, 0);
		pane.add(canvas, 0, 1);
		pane.add(btnPerform, 0, 2);

		// Displaying scene
		Scene scene = new Scene(pane, windowSizeX, windowSizeY);

		primaryStage.setTitle("ALDA A* Demo");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void renderGraph() {
		// clear area and set defaults
		gc.clearRect(0, 0, canvasSizeX, canvasSizeY);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);

		// drawing edges
		for (Edge edge : graph.getEdges()) {
			gc.setStroke(Color.GRAY);
			gc.setLineWidth(5);
			int fromX = edge.getNodeFrom().getPosX();
			int fromY = edge.getNodeFrom().getPosY();
			int toX = edge.getNodeTo().getPosX();
			int toY = edge.getNodeTo().getPosY();

			int middleX = fromX + ((toX - fromX) / 2);
			int middleY = fromY + ((toY - fromY) / 2);

			// draw the line
			gc.strokeLine(fromX, fromY, toX, toY);
			// TODO: Make these arrows at some time...

			// printing numbers
			gc.setFill(Color.DARKRED);
			Font font = Font.font("Verdana", FontWeight.BOLD, 14);
			gc.setFont(font);
			gc.fillText(Double.toString(edge.getCost()), middleX, middleY);
		}

		// drawing nodes
		for (Node node : graph.getNodes()) {
			// Circles
			gc.setFill(colorHTWG);
			gc.fillOval(node.getPosX() - 10, node.getPosY() - 10, 20, 20);

			// Text
			gc.setFill(Color.WHITE);
			Font font = Font.font("Verdana", FontWeight.BOLD, 14);
			gc.setFont(font);
			gc.fillText(node.getName(), node.getPosX(), node.getPosY());
		}

	}

}
