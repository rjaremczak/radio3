package com.mindpart.ui;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Popup;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.09
 */
public class ChartMarker {
    private Line verticalLine;
    private Circle selectionPoint;
    private Popup selectionPopup;
    private Label selectionLabel;
    private Pane referencePane;
    private XYChart<Number,Number> chart;
    private Bounds chartBounds;
    private Function<Point2D,SelectionData> selectionFunction;

    static public class SelectionData {
        Point2D pos;
        String text;

        public SelectionData(Point2D pos, String text) {
            this.text = text;
            this.pos = pos;
        }
    }

    public void initialize(Pane referencePane, XYChart<Number, Number> chart, Function<Point2D, SelectionData> selectionFunction) {
        this.referencePane = referencePane;
        this.chart = chart;
        this.selectionFunction = selectionFunction;

        selectionLabel = new Label();
        selectionLabel.setStyle("-fx-border-width: 1; -fx-border-color: darkred; -fx-text-fill: black; -fx-background-color: white");
        selectionLabel.setPadding(new Insets(10));

        selectionPopup = new Popup();
        selectionPopup.getContent().add(selectionLabel);
        selectionPopup.setAutoHide(false);
        selectionPopup.setAutoFix(true);
        selectionPopup.setConsumeAutoHidingEvents(false);

        selectionPoint = new Circle(0,0,5, Color.WHITE);
        selectionPoint.setStroke(Color.RED);

        verticalLine = new Line();
        verticalLine.setFill(Color.LIGHTCORAL);

        referencePane.getChildren().add(verticalLine);
        referencePane.getChildren().add(selectionPoint);

        captureEvents();
    }

    public void captureEvents() {
        chart.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> updateChartBounds());
        chart.setOnMouseClicked(this::onMouseClicked);
        chart.setOnMouseExited(this::onMouseExited);
        chart.setOnMouseEntered(this::onMouseEntered);
    }

    public void updateChartBounds() {
        Axis<Number> xAxis = chart.getXAxis();
        Axis<Number> yAxis = chart.getYAxis();
        Bounds xBounds = referencePane.sceneToLocal(xAxis.localToScene(xAxis.getBoundsInLocal()));
        Bounds yBounds = referencePane.sceneToLocal(yAxis.localToScene(yAxis.getBoundsInLocal()));
        chartBounds = new BoundingBox(xBounds.getMinX(), yBounds.getMinY(), 0, xBounds.getWidth(), yBounds.getHeight(), 0);

        verticalLine.setStartY(chartBounds.getMinY());
        verticalLine.setEndY(chartBounds.getMaxY());
    }

    public void onMouseClicked(MouseEvent event) {
        if(chart.getData().isEmpty()) { return; }

        Point2D scenePos = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D refPos = referencePane.sceneToLocal(scenePos);
        if(!chartBounds.contains(refPos.getX(), refPos.getY())) { return; }

        SelectionData selectionData = selectionFunction.apply(refPos);

        selectionLabel.setText(selectionData.text);
        Point2D selPopupScreenPos = referencePane.localToScreen(selectionData.pos);
        selectionPopup.show(referencePane, selPopupScreenPos.getX()+10, selPopupScreenPos.getY()+20);

        selectionPoint.setCenterX(selectionData.pos.getX());
        selectionPoint.setCenterY(selectionData.pos.getY());

        verticalLine.setStartX(selectionData.pos.getX());
        verticalLine.setEndX(selectionData.pos.getX());

        selectionPoint.setVisible(true);
        verticalLine.setVisible(true);
    }

    public void onMouseEntered(MouseEvent event) {
    }

    public void onMouseExited(MouseEvent event) {
        if(!chartBounds.contains(referencePane.sceneToLocal(event.getSceneX(),event.getSceneY()))) {
            selectionPopup.hide();
            selectionPoint.setVisible(false);
            verticalLine.setVisible(false);
        }
    }
}
