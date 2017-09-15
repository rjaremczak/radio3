package com.mindpart.radio3.ui;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static javafx.scene.layout.Priority.NEVER;
import static javafx.scene.layout.Priority.SOMETIMES;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.14
 */
public class PropertyGrid {
    private final GridPane gridPane;
    private int rowCounter = 0;

    public PropertyGrid() {
        gridPane = new GridPane();
        gridPane.getColumnConstraints().setAll(
            new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, null, null, false),
            new ColumnConstraints(10, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, NEVER, null, false),
            new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, SOMETIMES, null, false));
    }

    public void addProperty(String name, Node item) {
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Courier", 13));
        nameLabel.setTextFill(Color.GRAY);

        gridPane.add(nameLabel, 0, rowCounter);
        gridPane.add(item, 2, rowCounter);
        rowCounter++;
    }

    public Label addProperty(String name) {
        Label valueLabel = new Label();
        valueLabel.setFont(Font.font("Courier", FontWeight.BOLD, 13));
        addProperty(name, valueLabel);
        GridPane.setHalignment(valueLabel, HPos.RIGHT);
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
