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

    private final ObservableList<Data<X,Y>> horizontalRulers;
    private final ObservableList<Data<X,Y>> verticalRulers;

    public EnhancedLineChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        legendVisibleProperty().setValue(false);
        setAnimated(false);

        horizontalRulers = FXCollections.observableArrayList(data -> new Observable[] { data.YValueProperty() });
        horizontalRulers.addListener((InvalidationListener) observable -> layoutPlotChildren());
        
        verticalRulers = FXCollections.observableArrayList(data -> new Observable[] { data.XValueProperty() });
        verticalRulers.addListener((InvalidationListener) observable -> layoutPlotChildren());
    }

    private void addRuler(ObservableList<Data<X,Y>> list, Data<X,Y> ruler) {
        Line line = new Line();
        ruler.setNode(line);
        getPlotChildren().add(line);
        list.add(ruler);
    }

    public void addHorizontalRuler(Data<X,Y> ruler) {
        addRuler(horizontalRulers, ruler);
    }

    public void addVerticalRuler(Data<X,Y> ruler) {
        addRuler(verticalRulers, ruler);
    }

    private void removeRuler(ObservableList<Data<X,Y>> list, Data<X,Y> ruler) {
        getPlotChildren().remove(ruler);
        ruler.setNode(null);
        list.remove(ruler);
    }

    public void removeHorizontalRuler(Data<X,Y> ruler) {
        horizontalRulers.remove(ruler);
    }

    public void removeVerticalRuler(Data<X,Y> ruler) {
        verticalRulers.remove(ruler);
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();

        horizontalRulers.forEach(ruler -> {
            Line line = (Line) ruler.getNode();
            line.setStartX(0);
            line.setEndX(getBoundsInLocal().getWidth());
            double ypos = getYAxis().getDisplayPosition(ruler.getYValue()) + 0.5;
            line.setStartY(ypos);
            line.setEndY(ypos);
            line.toFront();
        });

        verticalRulers.forEach(ruler -> {
            Line line = (Line) ruler.getNode();
            double xpos = getXAxis().getDisplayPosition(ruler.getXValue()) + 0.5;
            line.setStartX(xpos);
            line.setEndX(xpos);
            line.setStartY(0);
            line.setEndY(getBoundsInLocal().getHeight());
            line.toFront();
        });
    }
}
