//    HelloIoT is a dashboard creator for MQTT
//    Copyright (C) 2017 Adrián Romero Corchado.
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

/**
 *
 * @author adrian
 */
public enum GaugeType {
    BASIC((min, max) -> {
        double sixth = (max - min) / 6.0;
        return GaugeBuilder.create()
                .minValue(min)
                .maxValue(max)
                .autoScale(false)
                .minorTickMarksVisible(false)
                .mediumTickMarksVisible(false)
                .majorTickSpace(1.0)
                .majorTickMarkType(TickMarkType.BOX)
                .gradientBarEnabled(true)
                .sections(new Section(min, min + sixth, Color.web("#11632f")),
                        new Section(min + sixth, min + sixth * 2, Color.web("#36843d")),
                        new Section(min + sixth * 2, min + sixth * 3, Color.web("#67a328")),
                        new Section(min + sixth * 3, min + sixth * 4, Color.web("#80b940")),
                        new Section(min + sixth * 4, min + sixth * 5, Color.web("#95c262")),
                        new Section(min + sixth * 5, max, Color.web("#badf8d")))
                .sectionsVisible(true)
                .decimals(1)
                .build();
    }),
    SPACEX((min, max) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SPACE_X)
                .backgroundPaint(Color.TRANSPARENT)
                .valueColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .minValue(min)
                .maxValue(max)
                .autoScale(false)
                .decimals(1)
                .build();
    }),
    FLAT((min, max) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.FLAT)
                .minValue(min)
                .maxValue(max)
                .prefSize(100.0, 100.0)
                .autoScale(false)
                .barColor(Color.BLUE)
                .decimals(1)
                .build();
    }),
    DASHBOARD((min, max) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.DASHBOARD)
                .minValue(min)
                .maxValue(max)
                .autoScale(false)
                .decimals(1)
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
    SIMPLE((min, max) -> {
        double sixth = (max - min) / 6.0;
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SIMPLE)
                .scaleDirection(ScaleDirection.CLOCKWISE)
                .sections(new Section(min, min + sixth, Color.web("#11632f")),
                        new Section(min + sixth, min + sixth * 2, Color.web("#36843d")),
                        new Section(min + sixth * 2, min + sixth * 3, Color.web("#67a328")),
                        new Section(min + sixth * 3, min + sixth * 4, Color.web("#80b940")),
                        new Section(min + sixth * 4, min + sixth * 5, Color.web("#95c262")),
                        new Section(min + sixth * 5, max, Color.web("#badf8d")))
                .decimals(1)
                .minValue(min)
                .maxValue(max)
                .autoScale(false)
                .titleColor(Color.LIGHTGRAY)
                .borderPaint(Color.LIGHTGRAY)
                .build();
    }),
    LINEAR((min, max) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.LINEAR)
                .minValue(min)
                .maxValue(max)
                .prefSize(100.0, 100.0)
                .autoScale(false)
                .orientation(Orientation.HORIZONTAL)
                .barColor(Color.BLUE)
                .decimals(1)
                .build();
    }),
    BAR((min, max) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.BAR)
                .minValue(min)
                .maxValue(max)
                .prefSize(100.0, 100.0)
                .autoScale(false)
                .barColor(Color.BLUE)
                .decimals(1)
                .animated(false)                
                .build();
    }),
    WHITE((min, max) -> {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.WHITE)
                .minValue(min)
                .maxValue(max)
                .prefSize(100.0, 100.0)
                .autoScale(false)
                .decimals(1)
                .animated(false)
                .build();
    });

    private final GaugeSupplier builder;

    private GaugeType(GaugeSupplier builder) {
        this.builder = builder;
    }

    public Gauge build(double min, double max) {
        return builder.get(min, max);
    }

    @FunctionalInterface
    private static interface GaugeSupplier {

        Gauge get(double min, double max);
    }
}
