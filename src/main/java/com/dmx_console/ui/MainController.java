package com.dmx_console.ui;

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import com.dmx_console.service.FixtureService;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
        javafx.scene.shape.Rectangle colorPreview =
                new javafx.scene.shape.Rectangle(200, 50);
        colorPreview.setFill(Color.BLACK);
        colorPreview.setArcWidth(10);
        colorPreview.setArcHeight(10);

        //Boton de blackout
        Button btnBlackout = new Button("BLACK OUT");
        btnBlackout.setStyle(
                "-fx-background-color: #333; -fx-text-fill: white;" +
                        "-fx-font-size: 14px; -fx-padding: 10 20;"
        );

        sliderPanel.getChildren().addAll(
                labelFixture,
                new Label("Red"), sliderR,
                new Label("Green"), sliderG,
                new Label("Blue"), sliderB,
                new Label("White"), sliderW,
                new Label("Storbe"), sliderStrobe,
                colorPreview,
                btnBlackout

        );

        // LOGICA CUANDO SE SELECCIONA UN FIXTURE
        fixtureList.getSelectionModel().selectedIndexProperty()
                .addListener((obs, oldVal, newVal) -> {
                    selectedFixture = rig.get(newVal.intValue());
                    labelFixture.setText(selectedFixture.getName()
                            + "- Dir: " + selectedFixture.getAddress()
                    );

                    // Reset Sliders

                    sliderR.setValue(0);
                    sliderG.setValue(0);
                    sliderB.setValue(0);
                    sliderW.setValue(0);
                    sliderY.setValue(0);
                    sliderStrobe.setValue(0);
                });


        // Logica que sucede cuando se desliza un slider

        Runnable updateDMX = () -> {
            if(selectedFixture == null ) return;
             int r = (int) sliderR.getValue();
             int g = (int) sliderG.getValue();
             int b = (int) sliderB.getValue();
             int w = (int) sliderW.getValue();
             int y = (int) sliderY.getValue();
             int strobe = (int) sliderStrobe.getValue();

             service.setColor(selectedFixture, r,g,b);
             service.setChannel(selectedFixture, ChannelFunction.WHITE, w);
             service.setChannel(selectedFixture, ChannelFunction.YELLOW, y);
             service.setChannel(selectedFixture, ChannelFunction.STROBE, strobe);

             colorPreview.setFill(Color.rgb(r,g,b));
        };

        sliderR.valueProperty().addListener((o, ov, nv) ->
                updateDMX.run());
        sliderG.valueProperty().addListener((o, ov, nv) ->
                updateDMX.run());
        sliderB.valueProperty().addListener((o,ov,nv) ->
                updateDMX.run());
        sliderW.valueProperty().addListener((o,ov,nv)->
                updateDMX.run());
        sliderY.valueProperty().addListener((o,ov,nv)->
                updateDMX.run());
        sliderStrobe.valueProperty().addListener((o,ov,nv)->
                updateDMX.run());

        // LOGICA DEL BLACKOUT

        btnBlackout.setOnAction(e ->{
            if(selectedFixture==null) return;
            service.blackout(selectedFixture);
            sliderR.setValue(0);
            sliderG.setValue(0);
            sliderB.setValue(0);
            sliderW.setValue(0);
            sliderY.setValue(0);
            sliderStrobe.setValue(0);
        });

        /// Final Layout
        view.setLeft(fixtureList);
        view.setCenter(sliderPanel);
        view.setStyle("-fx-background-color: #1e1e1e;");
        sliderPanel.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: white;");


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


