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
public class ChartGuides {
    private Line verticalGuide;
    private Circle selectionPoint;
    private Popup selectionPopup;
    private Label selectionLabel;
    private Pane referencePane;
    private XYChart<Number,Number> xyChart;
    private Bounds chartBounds;
    private Function<Point2D,SelectionData> selectionDataFunction;

    static public class SelectionData {
        Point2D pos;
        String text;

        public SelectionData(Point2D pos, String text) {
            this.text = text;
            this.pos = pos;
        }
    }

    public ChartGuides(Pane referencePane, XYChart<Number, Number> xyChart, Function<Point2D, SelectionData> selectionDataFunction) {
        this.referencePane = referencePane;
        this.xyChart = xyChart;
        this.selectionDataFunction = selectionDataFunction;

        selectionLabel = new Label();
        selectionLabel.setStyle("-fx-border-width: 1; -fx-border-color: black; -fx-text-fill: darkblue; -fx-background-color: white");
        selectionLabel.setPadding(new Insets(10));

        selectionPopup = new Popup();
        selectionPopup.getContent().add(selectionLabel);
        selectionPopup.setAutoHide(false);
        selectionPopup.setAutoFix(true);
        selectionPopup.setConsumeAutoHidingEvents(false);

        selectionPoint = new Circle(0,0,5, Color.WHITE);
        selectionPoint.setStroke(Color.RED);

        verticalGuide = new Line();
        verticalGuide.setFill(Color.LIGHTCORAL);

        referencePane.getChildren().add(verticalGuide);
        referencePane.getChildren().add(selectionPoint);
    }

    public void updateChartBounds() {
        Axis<Number> xAxis = xyChart.getXAxis();
        Axis<Number> yAxis = xyChart.getYAxis();
        Bounds xBounds = referencePane.sceneToLocal(xAxis.localToScene(xAxis.getBoundsInLocal()));
        Bounds yBounds = referencePane.sceneToLocal(yAxis.localToScene(yAxis.getBoundsInLocal()));
        chartBounds = new BoundingBox(xBounds.getMinX(), yBounds.getMinY(), 0, xBounds.getWidth(), yBounds.getHeight(), 0);

        verticalGuide.setStartY(chartBounds.getMinY());
        verticalGuide.setEndY(chartBounds.getMaxY());
    }

    public void onMouseClicked(MouseEvent event) {
        if(xyChart.getData().isEmpty()) { return; }

        selectionPoint.setDisable(false);
        verticalGuide.setDisable(false);

        Point2D scenePos = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D refPos = referencePane.sceneToLocal(scenePos);
        if(!chartBounds.contains(refPos.getX(), refPos.getY())) { return; }

        SelectionData selectionData = selectionDataFunction.apply(refPos);

        selectionLabel.setText(selectionData.text);
        Point2D selPopupScreenPos = referencePane.localToScreen(selectionData.pos);
        selectionPopup.show(referencePane, selPopupScreenPos.getX()+10, selPopupScreenPos.getY()+20);

        selectionPoint.setCenterX(selectionData.pos.getX());
        selectionPoint.setCenterY(selectionData.pos.getY());

        verticalGuide.setStartX(selectionData.pos.getX());
        verticalGuide.setEndX(selectionData.pos.getX());
    }

    public void onMouseExited(MouseEvent event) {
        if(!chartBounds.contains(referencePane.sceneToLocal(event.getSceneX(),event.getSceneY()))) {
            //selectionPopup.hide();
            //selectionPoint.setDisable(true);
            //verticalGuide.setDisable(true);
        }
    }
}
