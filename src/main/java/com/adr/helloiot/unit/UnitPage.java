//    This file is part of HelloIot.
//
//    HelloIot is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    HelloIot is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with HelloIot.  If not, see <http://www.gnu.org/licenses/>.

package com.adr.helloiot.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public class UnitPage implements Comparable<UnitPage> {
    
    private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("com/adr/helloiot/fxml/main");
    
    private final String name;
    private final Node graphic;
    private final String text;
    
    private int columns;
    private String emptylabel;
    private boolean system;
    private double maxwidth;
    private double maxheight;
    private int order = 10000;
    
    private final List<Node> units = new ArrayList<>();
    
    public UnitPage(String name, Node graphic, String text) {
        this.name = name;
        this.graphic = graphic;
        this.text = text;
        
        this.emptylabel = RESOURCES.getString("label.empty");
        this.columns = 5;
        this.system = false; // System units have the Unit pages menu disabled and do not appear in the menu too
        this.maxwidth = Double.MAX_VALUE;
        this.maxheight = Double.MAX_VALUE;
    }

    
    public String getName() {
        return name;
    }
    
    public Node getGraphic() {
        return graphic;
    }
    
    public String getText() {
        return text;
    }

    public void setColumns(int value) {
        columns = value;
    }
    
    public int getColumns() {
        return columns;
    }
    
    public void setSystem(boolean value) {
        system = value;
    }
    
    public boolean isSystem() {
        return system;
    }
    public void setEmptyLabel(String value) {
        emptylabel = value;
    }
    
    public String getEmptyLabel() {
        return emptylabel;
    }

    public double getMaxWidth() {
        return maxwidth;
    }

    public void setMaxWidth(double maxwidth) {
        this.maxwidth = maxwidth;
    }

    public double getMaxHeight() {
        return maxheight;
    }

    public void setMaxHeight(double maxheight) {
        this.maxheight = maxheight;
    }

    public void setMaxSize(double maxwidth, double maxheight) {
        this.maxwidth = maxwidth;
        this.maxheight = maxheight;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
    public List<Node> getUnits() {
        return units;
    }
    
    public static void setPage(Node node, String value) {
        if (value == null) {
            node.getProperties().remove("UnitPage");
        } else {
            node.getProperties().put("UnitPage", value);
        }
    }

    @Override
    public int compareTo(UnitPage o) {
        return Integer.compare(order, o.order);
    }
    
    public static String getPage(Node node) {
        if (node.hasProperties()) {
            String value = (String) node.getProperties().get("UnitPage");
            if (value != null) {
                return value;
            }
        }
        return null;
    }    
}
