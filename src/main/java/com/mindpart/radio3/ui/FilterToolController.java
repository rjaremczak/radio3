package com.mindpart.radio3.ui;

import com.mindpart.numeric.QFactorCalc;
import com.mindpart.types.Frequency;
import com.mindpart.ui.VerticalRuler;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.mindpart.radio3.ui.FilterInfoType.BANDPASS;
import static com.mindpart.radio3.ui.FilterInfoType.BANDSTOP;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.11
 */
public class FilterToolController {
    private static final NumberFormat FORMAT_Q_FACTOR = new DecimalFormat("0.0");
    private static final Color RULER_MAIN_COLOR = Color.DARKBLUE;
    private static final Color RULER_SIDE_COLOR = Color.DARKBLUE.deriveColor(0, 1, 1, 0.3);

    private final TitledPane titledPane;
    private final PropertyGrid propertyGrid;
    private final Label bandPeakFreq;
    private final Label bandwidth;
    private final Label qFactor;
    private final VerticalRuler rulerBandwidthStart;
    private final VerticalRuler rulerBandwidthEnd;
    private final VerticalRuler rulerPeakFreq;
    private final Pane referencePane;
    private final ChartContext chartContext;

    private ChoiceBox<FilterInfoType> filterInfoTypeChoiceBox;

    public FilterToolController(BundleData bundle, Pane referencePane, XYChart<Number, Number> chart, ChartContext chartContext) {
        this.referencePane = referencePane;
        this.chartContext = chartContext;
        
        filterInfoTypeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(BANDSTOP, BANDPASS));
        filterInfoTypeChoiceBox.setConverter(bundle.getGenericStringConverter());
        filterInfoTypeChoiceBox.getSelectionModel().select(BANDSTOP);
        filterInfoTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::onChangeFilterInfoType);

        propertyGrid = new PropertyGrid();
        propertyGrid.addProperty(bundle.resolve("info.bandfilter.type"), filterInfoTypeChoiceBox);
        propertyGrid.addRow(new Label());
        
        bandPeakFreq = propertyGrid.addProperty(bundle.resolve("info.bandfilter.freq"));
        bandwidth = propertyGrid.addProperty(bundle.resolve("info.bandfilter.width"));
        qFactor = propertyGrid.addProperty(bundle.resolve("info.bandfilter.q"));

        rulerBandwidthStart = new VerticalRuler(referencePane, chart, RULER_SIDE_COLOR);
        rulerPeakFreq = new VerticalRuler(referencePane, chart, RULER_MAIN_COLOR);
        rulerBandwidthEnd = new VerticalRuler(referencePane, chart, RULER_SIDE_COLOR);

        titledPane = new TitledPane(bundle.resolve("info.bandfilter.title"), propertyGrid.getNode());
        titledPane.setAlignment(Pos.TOP_LEFT);
        titledPane.setAnimated(false);
        titledPane.expandedProperty().addListener(this::onExpandedListener);

    }

    private void onExpandedListener(ObservableValue<? extends Boolean> ob, Boolean old, Boolean expanded) {
        clear();
        if(expanded) {
            update();
        }
    }

    private void onChangeFilterInfoType(ObservableValue<? extends FilterInfoType> ob, FilterInfoType old, FilterInfoType current) {
        clear();
        update(current);
    }

    public void setDisable(boolean disable) {
        titledPane.setExpanded(!disable);
        titledPane.setDisable(disable);
    }

    private void qUpdateAndShow(QFactorCalc qFactorCalc) {
        bandPeakFreq.setText(Frequency.ofMHz(qFactorCalc.getBandPeak()).format());
        bandwidth.setText(Frequency.ofMHz(qFactorCalc.getBandwidth()).format());
        qFactor.setText(FORMAT_Q_FACTOR.format(qFactorCalc.getQFactor()) + "    ");

        referencePane.layout();

        rulerBandwidthStart.updateAndShow(qFactorCalc.getBandStart());
        rulerBandwidthEnd.updateAndShow(qFactorCalc.getBandEnd());
        rulerPeakFreq.updateAndShow(qFactorCalc.getBandPeak());
    }

    public void update() {
        update(filterInfoTypeChoiceBox.getSelectionModel().getSelectedItem());
    }
    
    private void update(FilterInfoType filterInfoType) {
        if(titledPane.isExpanded() && chartContext.valueProcessor instanceof LogProbeProcessor) {
            QFactorCalc qFactorCalc = new QFactorCalc(chartContext.receivedFreq, chartContext.processedData);
            switch (filterInfoType) {
                case BANDPASS:
                    if(qFactorCalc.checkBandPass(3.0)) qUpdateAndShow(qFactorCalc);
                    break;
                case BANDSTOP:
                    if(qFactorCalc.checkBandStop(3.0)) qUpdateAndShow(qFactorCalc);
                    break;
            }
        }
    }

    public void clear() {
        bandPeakFreq.setText("");
        bandwidth.setText("");
        qFactor.setText("");
        rulerBandwidthStart.hide();
        rulerBandwidthEnd.hide();
        rulerPeakFreq.hide();
    }

    public TitledPane getTitledPane() {
        return titledPane;
    }
}