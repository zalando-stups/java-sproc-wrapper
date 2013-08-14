/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;

/**
 * @author  Jan
 */
public class ExampleIntCollection {
    @DatabaseField
    protected List<Integer> a;

    public List<Integer> getA() {
        return a;
    }

    public void setA(final List<Integer> a) {
        this.a = a;
    }
}
