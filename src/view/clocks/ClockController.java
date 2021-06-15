package view.clocks;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class ClockController {

    @FXML
    BorderPane airspeed, altitude, heading, yaw, roll, pitch;
    public HashMap<String,Gauge> gaugeMap;

    public Gauge gauge1;
    public Gauge gauge2;
    public Gauge gauge3;
    public Gauge gauge4;
    public Gauge gauge5;
    public Gauge gauge6;


    public void createClocks(){
        gauge1 = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(100, 100)
                .sections(new Section(85, 90, "", Color.rgb(204, 0, 0, 0.5)),
                        new Section(90, 95, "", Color.rgb(204, 0, 0, 0.75)),
                        new Section(95, 100, "", Color.rgb(204, 0, 0)))
                .sectionTextVisible(true)
                .minValue(0)
                .maxValue(100)
                .title("airSpeed")
                .unit("UNIT")
                .animated(true)
                .build();
        airspeed.setCenter(gauge1);

        gauge2 = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(100, 100)
                .sections(new Section(85, 90, "", Color.rgb(204, 0, 0, 0.5)),
                        new Section(90, 95, "", Color.rgb(204, 0, 0, 0.75)),
                        new Section(95, 100, "", Color.rgb(204, 0, 0)))
                .sectionTextVisible(true)
                .minValue(-1000)
                .maxValue(860)
                .title("altitude")
                .unit("UNIT")
                .animated(true)
                .build();
        altitude.setCenter(gauge2);

        gauge3 = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(100, 100)
                .sections(new Section(85, 90, "", Color.rgb(204, 0, 0, 0.5)),
                        new Section(90, 95, "", Color.rgb(204, 0, 0, 0.75)),
                        new Section(95, 100, "", Color.rgb(204, 0, 0)))
                .sectionTextVisible(true)
                .minValue(0)
                .maxValue(350)
                .title("heading")
                .unit("UNIT")
                .animated(true)
                .build();
        heading.setCenter(gauge3);

        gauge4 = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(100, 100)
                .sections(new Section(85, 90, "", Color.rgb(204, 0, 0, 0.5)),
                        new Section(90, 95, "", Color.rgb(204, 0, 0, 0.75)),
                        new Section(95, 100, "", Color.rgb(204, 0, 0)))
                .sectionTextVisible(true)
                .minValue(-30)
                .maxValue(90)
                .title("yaw")
                .unit("UNIT")
                .animated(true)
                .build();
        yaw.setCenter(gauge4);

        gauge5 = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(100, 100)
                .sections(new Section(85, 90, "", Color.rgb(204, 0, 0, 0.5)),
                        new Section(90, 95, "", Color.rgb(204, 0, 0, 0.75)),
                        new Section(95, 100, "", Color.rgb(204, 0, 0)))
                .sectionTextVisible(true)
                .minValue(-38)
                .maxValue(40)
                .title("roll")
                .unit("UNIT")
                .animated(true)
                .build();
        roll.setCenter(gauge5);

        gauge6 = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(100, 100)
                .sections(new Section(85, 90, "", Color.rgb(204, 0, 0, 0.5)),
                        new Section(90, 95, "", Color.rgb(204, 0, 0, 0.75)),
                        new Section(95, 100, "", Color.rgb(204, 0, 0)))
                .sectionTextVisible(true)
                .minValue(-10)
                .maxValue(17)
                .title("pitch")
                .unit("UNIT")
                .animated(true)
                .build();
        pitch.setCenter(gauge6);

        gaugeMap = new HashMap<>();
        gaugeMap.put("airSpeed",gauge1);
        gaugeMap.put("altitude",gauge2);
        gaugeMap.put("heading",gauge3);
        gaugeMap.put("yaw",gauge4);
        gaugeMap.put("roll",gauge5);
        gaugeMap.put("pitch",gauge6);
    }


}