package com.dmx_console.ui;

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.service.SceneService;
import javafx.beans.value.ChangeListener;
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
    private final SceneService sceneService;
    private final ScenePanel scenePanel;
    private ChangeListener<Number> dmxListener;
    private Slider sliderR;
    private Slider sliderG;
    private Slider sliderB;
    private Slider sliderY;
    private Slider sliderW;
    private Slider sliderStrobe;
    private Slider sliderDimmer;


    private Fixture selectedFixture;

    public MainController(List<Fixture> rig, FixtureService service){


        this.rig = rig;
        this.service = service;
        this.view = new BorderPane();
        this.sceneService = new SceneService(service);
        this.scenePanel = new ScenePanel(sceneService, rig, () -> updateSliders(selectedFixture));
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

         sliderDimmer = new Slider(0, 255, 255); //dimmer
         sliderR = createSlider();
         sliderG = createSlider();
         sliderB = createSlider();
         sliderW = createSlider();
         sliderY = createSlider();
         sliderStrobe = createSlider();

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
                new Label("Dimmer"), sliderDimmer, //dimmer
                new Label("Red"), sliderR,
                new Label("Green"), sliderG,
                new Label("Blue"), sliderB,
                new Label("White"), sliderW,
                new Label("Yellow"), sliderY,
                new Label("Strobe"), sliderStrobe,
                colorPreview,
                btnBlackout

        );

        // LOGICA CUANDO SE SELECCIONA UN FIXTURE
        fixtureList.getSelectionModel().selectedIndexProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if(newVal.intValue() < 0) return;
                    selectedFixture = rig.get(newVal.intValue());
                    labelFixture.setText(selectedFixture.getName()
                            + "- Dir: " + selectedFixture.getAddress()
                    );
                    updateSliders(selectedFixture);

                    // Reset Sliders

                    sliderDimmer.setValue(255); //dimmer

                });


        // Logica que sucede cuando se desliza un slider

        dmxListener = (o, ov, nv) -> {

            if(selectedFixture == null ) return;
             int r = (int) sliderR.getValue();
             int g = (int) sliderG.getValue();
             int b = (int) sliderB.getValue();
             int w = (int) sliderW.getValue();
             int y = (int) sliderY.getValue();
             int strobe = (int) sliderStrobe.getValue();
             int dimmer = (int) sliderDimmer.getValue();



            service.setColor(selectedFixture, r,g,b);
            service.setChannel(selectedFixture, ChannelFunction.WHITE, w);
            service.setChannel(selectedFixture, ChannelFunction.YELLOW, y);
            service.setChannel(selectedFixture, ChannelFunction.STROBE, strobe);
            service.setChannel(selectedFixture, ChannelFunction.DIMMER, dimmer);

            // modificando el preview utiliozando Yellow y White
            double dimmerFactor = sliderDimmer.getValue() / 255.0;
            int previewR = (int)(Math.min(255, r+y) * dimmerFactor);
            int previewG = (int)(Math.min(255, g + (y/2) + w) * dimmerFactor);
            int previewB = (int)(Math.min(255, b+w) * dimmerFactor);



             colorPreview.setFill(Color.rgb(previewR,previewG,previewB));
        };

        sliderDimmer.valueProperty().addListener(dmxListener);  // dimmer
        assert sliderR != null;
        sliderR.valueProperty().addListener(dmxListener);
        assert sliderG != null;
        sliderG.valueProperty().addListener(dmxListener);
        assert sliderB != null;
        sliderB.valueProperty().addListener(dmxListener);
        assert sliderW != null;
        sliderW.valueProperty().addListener(dmxListener);
        assert sliderY != null;
        sliderY.valueProperty().addListener(dmxListener);
        assert sliderStrobe != null;
        sliderStrobe.valueProperty().addListener(dmxListener);

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
        view.setRight(scenePanel.getView());
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

    public void updateSliders(Fixture fixture){
        if(fixture == null) return;

        //Deshabilidar listeners para evitar loops
        sliderR.valueProperty().removeListener(dmxListener);
        sliderG.valueProperty().removeListener(dmxListener);
        sliderB.valueProperty().removeListener(dmxListener);
        sliderY.valueProperty().removeListener(dmxListener);
        sliderW.valueProperty().removeListener(dmxListener);
        sliderStrobe.valueProperty().removeListener(dmxListener);
        sliderDimmer.valueProperty().removeListener(dmxListener);

        // Se actualizaran los valores visuales
        sliderDimmer.setValue(service.getChannelValue(fixture, ChannelFunction.DIMMER));
        sliderR.setValue(service.getChannelValue(fixture, ChannelFunction.RED));
        sliderG.setValue(service.getChannelValue(fixture, ChannelFunction.GREEN));
        sliderB.setValue(service.getChannelValue(fixture, ChannelFunction.BLUE));
        sliderW.setValue(service.getChannelValue(fixture, ChannelFunction.WHITE));
        sliderY.setValue(service.getChannelValue(fixture, ChannelFunction.YELLOW));
        sliderStrobe.setValue(service.getChannelValue(fixture, ChannelFunction.STROBE));

        // Se reconectan los listeners
        sliderR.valueProperty().addListener(dmxListener);
        sliderG.valueProperty().addListener(dmxListener);
        sliderB.valueProperty().addListener(dmxListener);
        sliderW.valueProperty().addListener(dmxListener);
        sliderY.valueProperty().addListener(dmxListener);
        sliderDimmer.valueProperty().addListener(dmxListener);
        sliderStrobe.valueProperty().addListener(dmxListener);



    }

    public Fixture getSelectedFixture(){
        return selectedFixture;

    }




}


