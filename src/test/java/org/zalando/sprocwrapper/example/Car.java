package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

public class Car {

    @DatabaseField
    private Long id;

    @DatabaseField
    private String brand;

    @DatabaseField
    private Color color;

    public Long getId() {
        return id;
    }

    public Car setId(Long id) {
        this.id = id;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public Car setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public Car setColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", color=" + color +
                '}';
    }
}
