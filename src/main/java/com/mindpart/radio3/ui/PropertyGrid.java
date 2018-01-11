package com.mindpart.radio3.ui;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.scene.layout.Priority.NEVER;
import static javafx.scene.layout.Priority.SOMETIMES;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.14
 */
public class PropertyGrid {
    private static final Font NAME_FONT = Font.font("Courier", 13);
    private static final Paint NAME_COLOR = Color.GRAY;
    private static final Font UNIT_FONT = Font.font("Courier", 13);
    private static final Paint UNIT_COLOR = Color.BLACK;
    private static final Font VALUE_FONT = Font.font("Courier", FontWeight.BOLD, 13);

    private final GridPane gridPane;
    private int rowCounter = 0;

    public PropertyGrid() {
        gridPane = new GridPane();
        gridPane.getColumnConstraints().setAll(
            new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, null, null, false),
            new ColumnConstraints(10, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, NEVER, null, false),
            new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, ALWAYS, null, false),
            new ColumnConstraints(10, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, NEVER, null, false),
            new ColumnConstraints(20, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, NEVER, null, false));
    }

    private Label createLabel(String text, Font font, Paint color) {
        Label label = new Label(text);
        label.setFont(font);
        label.setTextFill(color);
        return label;
    }

    public <T extends Node> T addProperty(String name, T item, String unit) {
        if(item instanceof Spinner) {
            ((Spinner)item).getEditor().setFont(VALUE_FONT);
        } else if(item instanceof Label) {
            ((Label)item).setFont(VALUE_FONT);
        } else if(item instanceof TextField) {
            ((TextField)item).setFont(VALUE_FONT);
        }

        GridPane.setHalignment(item, HPos.RIGHT);

        gridPane.add(createLabel(name, NAME_FONT, NAME_COLOR), 0, rowCounter);
        gridPane.add(item, 2, rowCounter);
        gridPane.add(createLabel(unit, UNIT_FONT, UNIT_COLOR), 4, rowCounter);
        rowCounter++;
        return item;
    }

    public Label addProperty(String name, String unit) {
        Label valueLabel = new Label();
        addProperty(name, valueLabel, unit);
        return valueLabel;
    }

    public void addRow(Node item) {
        gridPane.add(item, 0, rowCounter++, 3, 1);
    }

    public void addRow() {
        Label label = new Label();
        label.setFont(Font.font("Courier", 6));
        gridPane.add(label, 0, rowCounter++, 3, 1);
    }

    public Node getNode() {
        return gridPane;
    }
}
