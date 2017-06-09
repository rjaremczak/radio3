package com.mindpart.ui;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Popup;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.09
 */
public class ChartMarker {
    private Line selectionRuler;
    private Circle selectionPoint;
    private Line rangeEndRuler;
    private Line rangeStartRuler;
    private Popup selectionPopup;
    private Label selectionLabel;
    private Pane referencePane;
    private XYChart<Number,Number> chart;
    private NumberAxis chartAxisX;
    private NumberAxis chartAxisY;
    private Bounds chartBounds;
    private Function<Point2D,SelectionData> selectionHandler;
    private Consumer<XYChart.Data<Number, Number>> rangeStartHandler = null;
    private Consumer<XYChart.Data<Number, Number>> rangeEndHandler = null;
    private volatile boolean rangeStarted = false;
    private Supplier<Boolean> clickActive = () -> false;

    static public class SelectionData {
        Point2D pos;
        String text;

        public SelectionData(Point2D pos, String text) {
            this.text = text;
            this.pos = pos;
        }
    }

    private void initSelectionLabel() {
        selectionLabel = new Label();
        selectionLabel.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-background-color: white");
        selectionLabel.setPadding(new Insets(10));
        referencePane.getChildren().add(selectionLabel);
    }

    private void initSelectionPopup() {
        selectionPopup = new Popup();
        selectionPopup.getContent().add(selectionLabel);
        selectionPopup.setAutoHide(false);
        selectionPopup.setAutoFix(true);
        selectionPopup.setConsumeAutoHidingEvents(false);
    }

    private void initSelectionPoint() {
        selectionPoint = new Circle(0,0,5, Color.WHITE);
        selectionPoint.setStroke(Color.RED);
        referencePane.getChildren().add(selectionPoint);
    }

    private void initSelectionRuler() {
        selectionRuler = new Line();
        selectionRuler.setStroke(Color.LIGHTCORAL);
        referencePane.getChildren().add(selectionRuler);
    }

    private void initRangeRulers() {
        rangeEndRuler = new Line();
        rangeEndRuler.setStroke(Color.RED);

        rangeStartRuler = new Line();
        rangeStartRuler.setStroke(Color.RED);

        referencePane.getChildren().addAll(rangeStartRuler, rangeEndRuler);
    }

    public void initialize(Pane referencePane, XYChart<Number, Number> chart, Function<Point2D, SelectionData> selectionFunction, Supplier<Boolean> clickActive) {
        this.referencePane = referencePane;
        this.chart = chart;
        this.chartAxisX = (NumberAxis) chart.getXAxis();
        this.chartAxisY = (NumberAxis) chart.getYAxis();
        this.selectionHandler = selectionFunction;
        this.clickActive = clickActive;

        initSelectionLabel();
        initSelectionPopup();
        initSelectionRuler();
        initSelectionPoint();
        initRangeRulers();

        chart.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> updateChartBounds());
        chart.setOnMousePressed(this::onMousePressedOrDragged);
        chart.setOnMouseDragged(this::onMousePressedOrDragged);
        chart.setOnMouseReleased(this::onMouseReleased);
        chart.setOnMouseClicked(this::onMouseClicked);

        clear();
    }

    public void clear() {
        rangeStarted = false;
        selectionRuler.setVisible(false);
        selectionPoint.setVisible(false);
        selectionPopup.hide();
        rangeStartRuler.setVisible(false);
        rangeEndRuler.setVisible(false);
    }

    public void setupRangeSelection(Consumer<XYChart.Data<Number, Number>> startHandler,
                                    Consumer<XYChart.Data<Number, Number>> endHandler) {
        this.rangeStartHandler = startHandler;
        this.rangeEndHandler = endHandler;
    }

    private boolean isRangeSelection() {
        return rangeStartHandler!=null && rangeEndHandler!=null;
    }

    private void setupRuler(Line vLine) {
        vLine.setStartY(chartBounds.getMinY());
        vLine.setEndY(chartBounds.getMaxY());
    }

    private void updateRuler(Line vLine, double x, boolean visible) {
        vLine.setStartX(x);
        vLine.setEndX(x);
        vLine.setVisible(visible);
    }

    public void updateChartBounds() {
        Bounds xBounds = referencePane.sceneToLocal(chartAxisX.localToScene(chartAxisX.getBoundsInLocal()));
        Bounds yBounds = referencePane.sceneToLocal(chartAxisY.localToScene(chartAxisY.getBoundsInLocal()));
        chartBounds = new BoundingBox(xBounds.getMinX(), yBounds.getMinY(), 0, xBounds.getWidth(), yBounds.getHeight(), 0);

        setupRuler(selectionRuler);
        setupRuler(rangeEndRuler);
        setupRuler(rangeStartRuler);
    }

    private Point2D eventToRefPos(MouseEvent event) {
        return referencePane.sceneToLocal(event.getSceneX(), event.getSceneY());
    }

    private XYChart.Data<Number, Number> eventToValues(MouseEvent event) {
        return new XYChart.Data<>(
                chartAxisX.getValueForDisplay(chartAxisX.sceneToLocal(event.getSceneX(), 0).getX()),
                chartAxisY.getValueForDisplay(chartAxisY.sceneToLocal(0, event.getSceneY()).getY()));
    }

    private void onRangeStarted(Point2D refPos, MouseEvent event) {
        rangeStarted = true;
        updateRuler(rangeStartRuler, refPos.getX(), true);
        rangeEndRuler.setVisible(false);

        XYChart.Data<Number, Number> data = eventToValues(event);
        rangeStartHandler.accept(data);
        rangeEndHandler.accept(data);
    }

    private void onRangeEnded(Point2D refPos, MouseEvent event) {
        if(refPos.getX() > rangeStartRuler.getStartX()) {
            rangeStarted = false;
            updateRuler(rangeEndRuler, refPos.getX(), true);
            rangeEndHandler.accept(eventToValues(event));
        } else {
            onRangeStarted(refPos, event);
        }
    }

    private void handleRangeSelection(Point2D refPos, MouseEvent event) {
        if(rangeStarted) {
            onRangeEnded(refPos, event);
        } else {
            onRangeStarted(refPos, event);
        }
    }

    private void onMouseReleased(MouseEvent mouseEvent) {
        hideChartMarker();
    }

    private void onMousePressedOrDragged(MouseEvent mouseEvent) {
        if(isMouseOverChartArea(mouseEvent)) {
            showChartMarker(eventToRefPos(mouseEvent));
        } else {
            hideChartMarker();
        }
    }

    private void showChartMarker(Point2D refPos) {
        SelectionData selectionData = selectionHandler.apply(refPos);
        if(selectionData==null) return;

        selectionLabel.setText(selectionData.text);
        Point2D selPopupScreenPos = referencePane.localToScreen(selectionData.pos);
        selectionPopup.show(referencePane, selPopupScreenPos.getX()+10, selPopupScreenPos.getY()+20);

        selectionPoint.setCenterX(selectionData.pos.getX());
        selectionPoint.setCenterY(selectionData.pos.getY());
        selectionPoint.setVisible(true);

        updateRuler(selectionRuler, selectionData.pos.getX(), true);
    }

    private void hideChartMarker() {
        selectionPopup.hide();
        selectionPoint.setVisible(false);
        selectionRuler.setVisible(false);
    }

    private void onMouseClicked(MouseEvent event) {
        if(!clickActive.get()) { return; }
        if(chart.getData().isEmpty()) { return; }

        Point2D refPos = eventToRefPos(event);
        if(!chartBounds.contains(refPos)) { return; }

        if(event.getButton() == MouseButton.SECONDARY) {
            if(isRangeSelection()) { handleRangeSelection(refPos, event); }
        }
    }

    private boolean isMouseOverChartArea(MouseEvent mouseEvent) {
        return chartBounds.contains(referencePane.sceneToLocal(mouseEvent.getSceneX(),mouseEvent.getSceneY()));
    }
}