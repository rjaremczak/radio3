package com.mindpart.ui;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.06
 */
public class VerticalRuler {
    private final Pane referencePane;
    private final XYChart<Number,Number> chart;
    private final NumberAxis chartAxisX;
    private final NumberAxis chartAxisY;

    private Line ruler;
    private Bounds chartBounds;
    private double selectedFrequency;

    public VerticalRuler(Pane referencePane, XYChart<Number, Number> chart, Color color) {
        this.referencePane = referencePane;
        this.chart = chart;
        this.chartAxisX = (NumberAxis) chart.getXAxis();
        this.chartAxisY = (NumberAxis) chart.getYAxis();

        ruler = new Line();
        ruler.setStroke(color);
        this.referencePane.getChildren().add(ruler);
        this.chart.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> updateChartBounds());

        updateChartBounds();
        hide();
    }

    public void hide() {
        ruler.setVisible(false);
    }

    public void show() {
        ruler.setVisible(true);
    }

    private void updateChartBounds() {
        Bounds xBounds = referencePane.sceneToLocal(chartAxisX.localToScene(chartAxisX.getBoundsInLocal()));
        Bounds yBounds = referencePane.sceneToLocal(chartAxisY.localToScene(chartAxisY.getBoundsInLocal()));
        chartBounds = new BoundingBox(xBounds.getMinX(), yBounds.getMinY(), 0, xBounds.getWidth(), yBounds.getHeight(), 0);
        update();
    }

    public void updateAndShow(double frequency) {
        selectedFrequency = frequency;
        update();
        show();
    }

    private void update() {
        double posX = referencePane.sceneToLocal(chartAxisX.localToScene(chartAxisX.getDisplayPosition(selectedFrequency),0)).getX();
        ruler.setStartX(posX);
        ruler.setEndX(posX);
        ruler.setStartY(chartBounds.getMinY());
        ruler.setEndY(chartBounds.getMaxY());
    }
}
