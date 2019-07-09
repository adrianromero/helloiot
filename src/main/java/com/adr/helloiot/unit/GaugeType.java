//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017-2019 Adrián Romero Corchado.
//
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
//
package com.adr.helloiot.unit;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.ScaleDirection;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickMarkType;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

public enum GaugeType {
    BASIC((min, max, barColor) -> {
        double fith = (max - min) / 5.0;
        return GaugeBuilder.create()
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)                
                .autoScale(false)
                .minorTickMarksVisible(false)
                .mediumTickMarksVisible(false)
                .majorTickSpace(1.0)
                .majorTickMarkType(TickMarkType.BOX)
                .gradientBarEnabled(true)
                .sections(new Section(min, min + fith, barColor.deriveColor(20.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith, min + fith * 2, barColor.deriveColor(10.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 2, min + fith * 3, barColor.deriveColor(0.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 3, min + fith * 4, barColor.deriveColor(-10.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 4, max, barColor.deriveColor(-20.0, 1.0, 1.0, 1.0)))
                .sectionsVisible(true)
                .build();
    }),
    SPACEX((min, max, barColor) -> {
        return GaugeBuilder.create()
                .minValue(min)
                .maxValue(max)                
                .skinType(Gauge.SkinType.SPACE_X)
                .backgroundPaint(Color.TRANSPARENT)
                .valueColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .threshold(max)
                .prefSize(150.0, 150.0)                
                .autoScale(false)
                .barColor(barColor)
                .build();
    }),
    FLAT((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.FLAT)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .build();
    }),
    DASHBOARD((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.DASHBOARD)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)                
                .autoScale(false)
                .barColor(Color.CRIMSON)
                .valueColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .unitColor(Color.BLACK)
                .shadowsEnabled(true)
                .gradientBarEnabled(true)
                .gradientBarStops(new Stop(0.00, Color.BLUE),
                        new Stop(0.25, Color.CYAN),
                        new Stop(0.50, Color.LIME),
                        new Stop(0.75, Color.YELLOW),
                        new Stop(1.00, Color.RED))
                .build();
    }),
    SIMPLE((min, max, barColor) -> {
        double fith = (max - min) / 5.0;
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SIMPLE)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)               
                .autoScale(false)
                .scaleDirection(ScaleDirection.CLOCKWISE)
                .sections(new Section(min, min + fith, barColor.deriveColor(20.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith, min + fith * 2, barColor.deriveColor(10.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 2, min + fith * 3, barColor.deriveColor(0.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 3, min + fith * 4, barColor.deriveColor(-10.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 4, max, barColor.deriveColor(-20.0, 1.0, 1.0, 1.0)))
                .borderPaint(Color.DARKGRAY)
                .needleColor(Color.WHITE)
                .needleBorderColor(Color.valueOf("#474747"))
                .build();
    }),
    LINEAR((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.LINEAR)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .orientation(Orientation.HORIZONTAL)
                .barColor(barColor)
                .build();
    }),
    BAR((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.BAR)     
                .minValue(min)
                .maxValue(max)                 
                .prefSize(100.0, 100.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();
    }),
    WHITE((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.WHITE)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)
                .build();
    }),
    INDICATOR((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.INDICATOR)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)
                .build();
    }),
    LEVEL((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.LEVEL)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)
                .build();
    }),
    TINY((min, max, barColor) -> {
        double fith = (max - min) / 5.0;
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.TINY)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .sections(new Section(min, min + fith, barColor.deriveColor(20.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith, min + fith * 2, barColor.deriveColor(10.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 2, min + fith * 3, barColor.deriveColor(0.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 3, min + fith * 4, barColor.deriveColor(-10.0, 1.0, 1.0, 1.0)),
                        new Section(min + fith * 4, max, barColor.deriveColor(-20.0, 1.0, 1.0, 1.0)))                
                .animated(false)               
                .build();
    }),
    BULLET((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.BULLET_CHART)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    DIGITAL((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    SLIM((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SLIM)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    AMP((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.AMP)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    KPI((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.KPI)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    MODERN((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    QUARTER((min, max, barColor) -> {     
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.QUARTER)
                .minValue(min)
                .maxValue(max)                 
                .prefSize(150.0, 150.0)
                .autoScale(false)   
                .animated(false)               
                .build();        
    }),
    LCD((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.LCD)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    SECTION((min, max, barColor) -> {
         return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SECTION)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();        
    }),
    SIMPLE_SECTION((min, max, barColor) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SIMPLE_SECTION)
                .minValue(min)
                .maxValue(max)
                .prefSize(150.0, 150.0)
                .autoScale(false)
                .barColor(barColor)
                .animated(false)               
                .build();            
    });

    private final GaugeSupplier builder;

    private GaugeType(GaugeSupplier builder) {
        this.builder = builder;
    }

    public Gauge build(double min, double max, Color barColor) {
        return builder.get(min, max, barColor);
    }

    @FunctionalInterface
    private static interface GaugeSupplier {
        Gauge get(double min, double max, Color barColor);
    }
}
