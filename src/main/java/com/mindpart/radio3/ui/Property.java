package com.mindpart.radio3.ui;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class Property {
    private final SimpleStringProperty name;
    private final SimpleStringProperty value;

    public Property(String name, String value) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(value);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    @Override
    public boolean equals(Object o) {
        Property property = (Property) o;
        return name.equals(property.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
