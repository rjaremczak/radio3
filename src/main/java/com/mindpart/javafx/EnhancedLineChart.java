package com.mindpart.javafx;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.shape.Line;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.16
 */
public class EnhancedLineChart<X,Y> extends LineChart<X,Y> {

    private final ObservableList<ChartRuler<Y>> horizontalRulers;
    private final ObservableList<ChartRuler<X>> verticalRulers;

    public EnhancedLineChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        legendVisibleProperty().setValue(false);
        setAnimated(false);

        horizontalRulers = FXCollections.observableArrayList(ruler -> new Observable[] { ruler.valueProperty() });
        horizontalRulers.addListener((InvalidationListener) observable -> layoutPlotChildren());
        
        verticalRulers = FXCollections.observableArrayList(ruler -> new Observable[] { ruler.valueProperty() });
        verticalRulers.addListener((InvalidationListener) observable -> layoutPlotChildren());
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

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();

        horizontalRulers.forEach(ruler -> {
            Line line = (Line) ruler.getNode();
            line.setStartX(0);
            line.setEndX(getBoundsInLocal().getWidth());
            double ypos = getYAxis().getDisplayPosition(ruler.getValue()) + 0.5;
            line.setStartY(ypos);
            line.setEndY(ypos);
            line.toFront();
        });

        verticalRulers.forEach(ruler -> {
            Line line = (Line) ruler.getNode();
            double xpos = getXAxis().getDisplayPosition(ruler.getValue()) + 0.5;
            line.setStartX(xpos);
            line.setEndX(xpos);
            line.setStartY(0);
            line.setEndY(getBoundsInLocal().getHeight());
            line.toFront();
        });
    }
}
