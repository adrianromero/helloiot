/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.helloiot.unit;

import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public interface UnitLine extends Unit {

    public ObservableList<Node> getChildren();
}
