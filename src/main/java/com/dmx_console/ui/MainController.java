package com.dmx_console.ui;

import com.dmx_console.model.Fixture;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import com.dmx_console.service.FixtureService;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainController {

    private final List<Fixture> rig;
    private final FixtureService service;
    private final BorderPane view;

    private Fixture selectedFixture;

    public MainController(List<Fixture> rig, FixtureService service){

        this.rig = rig;
        this.service = service;
        this.view = new BorderPane();
        buildUI();
    }

    private void buildUI(){

        // Modelar panel izquierdo - Lista de Fixtures
        ListView<String> fixtureList = new ListView<>();
        for (Fixture f: rig){
            fixtureList.getItems().add(f.getName());
        }
        fixtureList.setPrefWidth(160);

        // modelando panel central - sliders de cada canal
        VBox sliderPanel = new VBox(12);
        sliderPanel.setPadding(new Insets(20));

        Label labelFixture = new Label("Select Fixture");
        labelFixture.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Slider sliderR = createSlider();
        Slider sliderG = createSlider();
        Slider sliderB = createSlider();
        Slider sliderW = createSlider();
        Slider sliderY = createSlider();
        Slider sliderStrobe = createSlider();

        // Preview del color


    }

    public Slider createSlider(){
        Slider slider = new Slider(0, 255, 0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(64);
        slider.setPrefWidth(400);
        return slider;
    }

    public BorderPane getView(){
        return view;
    }
}


