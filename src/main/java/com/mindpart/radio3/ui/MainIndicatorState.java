package com.mindpart.radio3.ui;

import javafx.scene.paint.Color;

/**
 * Created by Robert Jaremczak
 * Date: 2017.02.26
 */
public enum MainIndicatorState {
    DISCONNECTED(Color.GRAY), CONNECTED(Color.LIGHTGREEN), PROCESSING(Color.ORANGE), ERROR(Color.RED);

    private Color color;

    public Color getColor() {
        return color;
    }

    MainIndicatorState(Color color) {
        this.color = color;
    }
}
