package com.mindpart.javafx;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.16
 */
public class EnhancedLineChart<X,Y> extends LineChart<X,Y> {

    private final ObservableList<ChartRuler<Y>> horizontalRulers;
    private final ObservableList<ChartRuler<X>> verticalRulers;
    private final ObservableList<ChartSpanMarker<X>> spanMarkers;

    public EnhancedLineChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        legendVisibleProperty().setValue(false);
        horizontalZeroLineVisibleProperty().setValue(false);
        setAnimated(false);

        horizontalRulers = FXCollections.observableArrayList(ruler -> new Observable[] { ruler.valueProperty() });
        horizontalRulers.addListener((InvalidationListener) observable -> layoutPlotChildren());
        
        verticalRulers = FXCollections.observableArrayList(ruler -> new Observable[] { ruler.valueProperty() });
        verticalRulers.addListener((InvalidationListener) observable -> layoutPlotChildren());

        spanMarkers = FXCollections.observableArrayList(marker -> new Observable[] { marker.minValueProperty(), marker.maxValueProperty() });
        spanMarkers.addListener((InvalidationListener) observable -> layoutPlotChildren());
    }

    public void addHorizontalRuler(ChartRuler<Y> chartRuler) {
        assert !horizontalRulers.contains(chartRuler);

        getPlotChildren().add(chartRuler.getNode());
        horizontalRulers.add(chartRuler);
    }

    public void addVerticalRuler(ChartRuler<X> chartRuler) {
        assert !verticalRulers.contains(chartRuler);
        
        getPlotChildren().add(chartRuler.getNode());
        verticalRulers.add(chartRuler);
    }

    private <T extends Object> void removeRuler(ObservableList<ChartRuler<T>> list, ChartRuler<T> ruler) {
        getPlotChildren().remove(ruler.getNode());
        list.remove(ruler);
    }

    public void removeHorizontalRuler(ChartRuler<Y> ruler) {
        getPlotChildren().remove(ruler.getNode());
        horizontalRulers.remove(ruler);
    }

    public void removeVerticalRuler(ChartRuler<X> ruler) {
        if(ruler!=null) {
            getPlotChildren().remove(ruler.getNode());
            horizontalRulers.remove(ruler);
        }
    }

    public void addSpanMarker(ChartSpanMarker<X> marker) {
        assert !spanMarkers.contains(marker);

        getPlotChildren().add(marker.getNode());
        spanMarkers.add(marker);
    }

    public void removeSpanMarker(ChartSpanMarker<X> marker) {
        getPlotChildren().remove(marker.getNode());
        spanMarkers.remove(marker);
    }

    private void layoutHorizontalRulers() {
        horizontalRulers.forEach(ruler -> {
            double ypos = getYAxis().getDisplayPosition(ruler.getValue());
            ruler.update(0, ypos, getBoundsInLocal().getWidth(), ypos);
        });
    }

    private void layoutVerticalRulers() {
        verticalRulers.forEach(ruler -> {
                double xpos = getXAxis().getDisplayPosition(ruler.getValue());
                ruler.update(xpos, 0, xpos, getBoundsInLocal().getHeight());
        });
    }

    private void layoutSpanMarkers() {
        spanMarkers.forEach(marker -> marker.update(
                getXAxis().getDisplayPosition(marker.getMinValue()), 0,
                getXAxis().getDisplayPosition(marker.getMaxValue()), getBoundsInLocal().getHeight()));
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        layoutSpanMarkers();
        layoutVerticalRulers();
        layoutHorizontalRulers();
    }
}
