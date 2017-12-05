package com.mindpart.radio3.ui;

import com.mindpart.javafx.ChartRuler;
import com.mindpart.javafx.ChartSpanMarker;
import com.mindpart.javafx.EnhancedLineChart;
import com.mindpart.numeric.QFactorCalc;
import com.mindpart.ui.IntegerSpinner;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.mindpart.radio3.ui.FilterInfoType.BANDPASS;
import static com.mindpart.radio3.ui.FilterInfoType.BANDSTOP;
import static com.mindpart.science.UnitPrefix.NANO;
import static com.mindpart.science.UnitPrefix.PICO;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.11
 */
public class FilterToolController {
    private static final NumberFormat FORMAT_FREQ = new DecimalFormat("0.000000");
    private static final NumberFormat FORMAT_L = new DecimalFormat("0.0");
    private static final NumberFormat FORMAT_R = new DecimalFormat("0.0");

    private static final Color RULER_MAIN_COLOR = Color.DARKBLUE;
    private static final Color RULER_SIDE_COLOR = Color.DARKBLUE.deriveColor(0, 1, 1, 0.1);

    private final TitledPane titledPane;
    private final PropertyGrid propertyGrid;
    private final Label bandPeakFreq;
    private final Label bandwidth;
    private final Label qFactor;
    private final IntegerSpinner capacitance;
    private final Label inductance;
    private final Label resistance;

    private final EnhancedLineChart<Number, Number> signalChart;
    private final ChartContext chartContext;
    private final ChartRuler<Number> rulerBandwidthStart;
    private final ChartRuler<Number> rulerBandwidthEnd;
    private final ChartRuler<Number> rulerPeakFreq;
    private final ChartSpanMarker<Number> bandwidthMarker;
    private final Collection<ChartRuler<Number>> allRulers = new ArrayList<>();
    private final ChoiceBox<FilterInfoType> filterInfoTypeChoiceBox;

    private QFactorCalc qFactorCalc;

    public FilterToolController(UserInterface ui, EnhancedLineChart<Number, Number> signalChart, ChartContext chartContext) {
        this.signalChart = signalChart;
        this.chartContext = chartContext;
        
        filterInfoTypeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(BANDSTOP, BANDPASS));
        filterInfoTypeChoiceBox.setConverter(ui.getGenericStringConverter());
        filterInfoTypeChoiceBox.getSelectionModel().select(BANDSTOP);
        filterInfoTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::onChangeFilterType);

        propertyGrid = new PropertyGrid();
        propertyGrid.addProperty(ui.text("info.bandfilter.type"), filterInfoTypeChoiceBox);
        propertyGrid.addRow();
        
        bandPeakFreq = propertyGrid.addProperty("f₀ [MHz]");
        bandwidth = propertyGrid.addProperty("B [MHz]");
        qFactor = propertyGrid.addProperty("Q");
        propertyGrid.addRow();

        capacitance = propertyGrid.addProperty("C [pF]", new IntegerSpinner());
        capacitance.getEditor().setFont(Font.font("Courier", FontWeight.BOLD, 13));
        propertyGrid.addRow();

        inductance = propertyGrid.addProperty("L [nH]");
        resistance = propertyGrid.addProperty("R [Ω]");
        capacitance.valueProperty().addListener((observable, oldValue, newValue) -> updateLR());

        titledPane = new TitledPane(ui.text("info.bandfilter.title"), propertyGrid.getNode());
        titledPane.setAlignment(Pos.TOP_LEFT);
        titledPane.setAnimated(false);
        titledPane.expandedProperty().addListener(this::onExpandedListener);

        rulerBandwidthStart = chartRuler(RULER_SIDE_COLOR);
        rulerPeakFreq = chartRuler(RULER_MAIN_COLOR);
        rulerBandwidthEnd = chartRuler(RULER_SIDE_COLOR);
        allRulers.addAll(Arrays.asList(rulerPeakFreq));

        bandwidthMarker = new ChartSpanMarker<>(0, 0, RULER_SIDE_COLOR);
    }

    private ChartRuler<Number> chartRuler(Color color) {
        Line line = new Line();
        line.setStroke(color);
        return new ChartRuler<>(0, line);
    }

    private void onExpandedListener(ObservableValue<? extends Boolean> ob, Boolean old, Boolean expanded) {
        if(expanded) {
            on();
            update();
        } else {
            off();
        }
    }

    private void onChangeFilterType(ObservableValue<? extends FilterInfoType> ob, FilterInfoType old, FilterInfoType current) {
        clear();
        update();
    }

    public void off() {
        clear();
        removeRulers();
    }

    public void on() {
        addRulers();
        clear();
    }

    public void setDisable(boolean disable) {
        titledPane.setExpanded(false);
        titledPane.setDisable(disable);
    }

    private void show() {
        bandPeakFreq.setText(FORMAT_FREQ.format(qFactorCalc.getBandPeak()));
        bandwidth.setText(FORMAT_FREQ.format(qFactorCalc.getBandwidth()));
        qFactor.setText(Long.toString(Math.round(qFactorCalc.getQFactor())));

        rulerBandwidthStart.setPosition(qFactorCalc.getBandStart());
        rulerBandwidthEnd.setPosition(qFactorCalc.getBandEnd());
        rulerPeakFreq.setPosition(qFactorCalc.getBandPeak());
        bandwidthMarker.setSpan(qFactorCalc.getBandStart(), qFactorCalc.getBandEnd());

        updateLR();

        showRulers(true);
    }

    private void updateLR() {
        Integer c_pF = capacitance.getValue();
        if(c_pF!=null && qFactorCalc!=null) {
            double c = PICO.toBase(c_pF);
            double omega = 2 * Math.PI * qFactorCalc.getBandPeak() * 1E6;
            double l = 1 / (omega * omega * c);
            double r = (omega * l) / qFactorCalc.getQFactor();

            inductance.setText(FORMAT_L.format(NANO.fromBase(l)));
            resistance.setText(FORMAT_R.format(r));
        }
    }

    public void update() {
        if(titledPane.isExpanded() && chartContext.isReady()) {
            qFactorCalc = new QFactorCalc(chartContext.receivedFreq, chartContext.processedData);
            switch (filterInfoTypeChoiceBox.getSelectionModel().getSelectedItem()) {
                case BANDPASS:
                    if(qFactorCalc.findBandPass(3.0)) show(); else clear();
                    break;
                case BANDSTOP:
                    if(qFactorCalc.findBandStop(3.0)) show(); else clear();
                    break;
                default:
                    clear();
            }
        }
    }

    private void addRulers() {
        allRulers.forEach(signalChart::addVerticalRuler);
        signalChart.addSpanMarker(bandwidthMarker);
    }

    private void removeRulers() {
        allRulers.forEach(signalChart::removeVerticalRuler);
        signalChart.removeSpanMarker(bandwidthMarker);
    }

    private void showRulers(boolean visible) {
        allRulers.forEach(r -> r.getNode().setVisible(visible));
        bandwidthMarker.getNode().setVisible(visible);
    }

    public void clear() {
        bandPeakFreq.setText("");
        bandwidth.setText("");
        qFactor.setText("");
        inductance.setText("");
        resistance.setText("");
        showRulers(false);
    }

    public TitledPane getTitledPane() {
        return titledPane;
    }
}