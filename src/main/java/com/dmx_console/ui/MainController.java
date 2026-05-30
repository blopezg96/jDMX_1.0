package com.dmx_console.ui;

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.service.SceneService;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.dmx_console.service.FixtureService;
import javafx.scene.paint.Color;

import java.util.List;

import static com.dmx_console.ui.Colors.BG_CARD;
import static com.dmx_console.ui.Colors.BG_ELEVATED;

public class MainController {

    private static final String BG_BASE = "#0a0a0f";
    private static final String BG_PANEL = "#12121a";
    private static final String BG_CARD = "#1a1a28";
    private static final String BG_ELEVATED = "#22223a";
    private static final String ACCENT = "#f0a500";
    private static final String ACCENT_DIM = "#7a5200";
    private static final String TEXT_PRIMARY = "#e8e8f0";
    private static final String TEXT_MUTED = "#6a6a8a";
    private static final String RED_CH = "#ff3a3a";
    private static final String GREEN_CH = "#3aff6a";
    private static final String BLUE_CH = "#3a8aff";
    private static final String WHITE_CH = "#e8e8e8";
    private static final String YELLOW_CH = "#ffe53a";
    private static final String STROBE_CH = "#aa3aff";
    private static final String DIM_CH = "#ff8c3a";

    private final List<Fixture> rig;
    private final FixtureService service;
    private final BorderPane view;
    private final SceneService sceneService;
    private final ScenePanel scenePanel;
    private ChangeListener<Number> dmxListener;
    private javafx.scene.shape.Rectangle colorPreview;
    private Slider sliderR;
    private Slider sliderG;
    private Slider sliderB;
    private Slider sliderY;
    private Slider sliderW;
    private Slider sliderStrobe;
    private Slider sliderDimmer;
    private HBox faderBank;
    private int currentPage = 1;


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

        HBox toolbar = new HBox();
        toolbar.setPadding(new Insets(8,16,8,16));
        toolbar.setAlignment(Pos.BASELINE_LEFT);
        toolbar.setStyle(
                "-fx-background-color: " + BG_PANEL + ";" +
                        "-fx-border-color: " + ACCENT_DIM + ";" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label appTitle = new Label("jDMX");
        appTitle.setStyle(
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + ACCENT + ";"+
                "-fx-letter-spacing: 4px;"

        );

        Label appSubtitle = new Label("CONSOLE v1.0");
        appSubtitle.setStyle(
                "-fx-font-family: 'Courier New';" +
                "-fx-font-size: 10px;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-padding: 0 0 0 8;"

                );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // INDICADOR DE DMX UNIVERSE
        Label universeLabel = new Label("[] UNIVERSE 1");
        universeLabel.setStyle("""
                -fx-font-family: 'Courier New';
                -fx-font-size: 11px;
                -fx-text-fill: #3aff6a; 
                -fx-padding: 0 20 0 0;
              
                """);

        /// Boton de blackout general
        Button blackOutAll = new Button("xxx BLACKOUT ALL xxx");
        blackOutAll.setStyle("""
                -fx-background-color: #8b0000;
                -fx-text-fill: white;
                -fx-font-family: 'Courier New';
                -fx-font-size: 11px;
                -fx-font-weight: bold;
                -fx-padding: 6 16;
                -fx-background-radius: 2;
                -fx-cursor: hand;

                
                """);

        blackOutAll.setOnAction(e -> service.blackoutAll());

        toolbar.getChildren().addAll(
                appTitle, appSubtitle, spacer, universeLabel, blackOutAll
        );


        // Modelar panel izquierdo - Lista de Fixtures

         VBox leftPanel = new VBox(0);
         leftPanel.setMinWidth(170);
         leftPanel.setMaxWidth(200);
         leftPanel.setStyle("-fx-background-color: " + BG_PANEL + ";");

         Label fixturesTitle = new Label("FIXTURES");
         fixturesTitle.setStyle(
                 "-fx-font-family: 'Courier New';" +
                         "-fx-font-size: 10px;" +
                         "-fx-text-fill: " + TEXT_MUTED + ";" +
                         "-fx-padding: 10 12 6 12;" +
                         "-fx-font-weight: bold;"


         );


        ListView<Fixture> fixtureList = new ListView<>();
        fixtureList.getItems().addAll(rig);
        fixtureList.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent;"
        );
        VBox.setVgrow(fixtureList, Priority.ALWAYS);                              /////////////////// MODIFICACION

        fixtureList.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Fixture item, boolean empty){
                if(empty || item ==null){
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-border-color: transparent; ");
                } else {
                    HBox cell = new HBox(8);
                    cell.setAlignment(Pos.CENTER_LEFT);

                    /// ICONO COLORIDO DE FIXTURE

                javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(5);
                dot.setFill(Color.web("#333355"));
                dot.setId("dot_" + item.getName());

                Label name = new Label(item.getName());
                name.setStyle(
                        "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 12px;" +
                                "-fx-text-fill: " + TEXT_PRIMARY + ";"
                );

                Label addr = new Label("@" + item.getAddress());
                addr.setStyle(
                        "-fx-font-fsmily: 'Courier New';" +
                                "-fx-font-size: 10px;" +
                                "-fx-text-fill: " + TEXT_MUTED + ";"
                );

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                cell.getChildren().addAll(dot, name, sp, addr);
                setGraphic(cell);
                setText(null);
                setStyle(
                        "-fx-background-color: " +
                                (isSelected() ? BG_ELEVATED: "transparent")  +
                                ";" + "-fx-padding: 6 8;"
                    );
                }
            }
        });

        leftPanel.getChildren().addAll(fixturesTitle, fixtureList);

        // modelando panel central - sliders de cada canal

        VBox centerPanel = new VBox(0);
        centerPanel.setStyle("-fx-background-color: " + BG_BASE + ";");
        VBox.setVgrow(centerPanel, Priority.ALWAYS);

        // Headerde fixture seleccionado}
        HBox fixtureHeader = new HBox(12);
        fixtureHeader.setPadding(new Insets(12,16,12,16));
        fixtureHeader.setAlignment(Pos.CENTER_LEFT);
        fixtureHeader.setStyle(
                "-fx-background-color: " +BG_CARD + ";"+
                        "-fx-border-color: " +BG_ELEVATED +";" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label labelFixture = new Label("-SELECT A FIXTURE-");
        labelFixture.setStyle(
                "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 14px;"+
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +ACCENT + ";"
        );

        Label labelAddr = new Label("");
        labelAddr.setStyle("-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setStyle("-fx-color-label-visible: false");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        fixtureHeader.getChildren().addAll(
                labelFixture, labelAddr, headerSpacer, colorPicker
        );

        // //////////////////////////////// Preview del color ////////////////////////////////////////////////////////
        colorPreview = new javafx.scene.shape.Rectangle(10, 60);
        colorPreview.setFill(Color.BLACK);
        colorPreview.setArcWidth(4);
        colorPreview.setArcHeight(4);
        colorPreview.widthProperty().bind(centerPanel.widthProperty());


        /////////////////////////////////////////////// FADERS ///////////////////////////////////////////////////////

        sliderDimmer = createFaderSlider();
        sliderR = createFaderSlider();
        sliderG = createFaderSlider();
        sliderB = createFaderSlider();
        sliderW = createFaderSlider();
        sliderY = createFaderSlider();
        sliderStrobe = createFaderSlider();

        HBox fadersRow = new HBox(6);
        fadersRow.setPadding(new Insets(20, 16, 20, 16));
        fadersRow.setAlignment(Pos.BOTTOM_CENTER);
        fadersRow.setStyle("-fx-background-color: "+BG_BASE + ";");
        VBox.setVgrow(fadersRow, Priority.ALWAYS);

        fadersRow.getChildren().addAll(
                buildFaderColumn("DIM", sliderDimmer, DIM_CH),
                buildSeparator(),
                buildFaderColumn("RED", sliderR, RED_CH),
                buildSeparator(),
                buildFaderColumn("GRN", sliderG, GREEN_CH),
                buildSeparator(),
                buildFaderColumn("BLU", sliderB, BLUE_CH),
                buildSeparator(),
                buildFaderColumn("YLW", sliderY, YELLOW_CH),
                buildSeparator(),
                buildFaderColumn("WHT", sliderW, WHITE_CH),
                buildSeparator(),
                buildFaderColumn("STRB", sliderStrobe, STROBE_CH)
        );

        /// Boton blackOut fixture

        Button btnBlackout = new Button("BLACKOUT FIXTURE");
        btnBlackout.setMaxWidth(Double.MAX_VALUE);
        btnBlackout.setStyle("-fx-background-color: #2a0000;" +
                "-fx-text-fill: #ff666;" +
                "-fx-font-family: 'Courier New';" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 0;" +
                "-fx-cursor: hand;"

        );

        centerPanel.getChildren().addAll(
                fixtureHeader,
                colorPreview,
                fadersRow,
                btnBlackout
        );

        // LOGICA CUANDO SE SELECCIONA UN FIXTURE
        fixtureList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if(newVal == null) return;
                    selectedFixture = newVal;
                    labelFixture.setText(newVal.getName());
                    labelAddr.setText("DMX @" + newVal.getAddress()
                            + "." + newVal.getProfile().getName());

                    updateSliders(selectedFixture);

                });

        /// dmx listener

        dmxListener = (o, ov, nv) -> {

            if (selectedFixture == null) return;
            int r = (int) sliderR.getValue();
            int g = (int) sliderG.getValue();
            int b = (int) sliderB.getValue();
            int w = (int) sliderW.getValue();
            int y = (int) sliderY.getValue();
            int strobe = (int) sliderStrobe.getValue();
            int dimmer = (int) sliderDimmer.getValue();


            service.setColor(selectedFixture, r, g, b);
            service.setChannel(selectedFixture, ChannelFunction.WHITE, w);
            service.setChannel(selectedFixture, ChannelFunction.YELLOW, y);
            service.setChannel(selectedFixture, ChannelFunction.STROBE, strobe);
            service.setChannel(selectedFixture, ChannelFunction.DIMMER, dimmer);
            updatePreview();

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

        /// blackout fixture
        btnBlackout.setOnAction(e -> {
            if(selectedFixture == null) return;
            service.blackout(selectedFixture);
            updateSliders(selectedFixture);
        });

        /// Color picker
        colorPicker.setOnHidden(e ->
            javafx.application.Platform.runLater(() -> {
                javafx.stage.Stage stage =
                        (javafx.stage.Stage) view.getScene().getWindow();
                javafx.animation.FadeTransition ft =
                        new javafx.animation.FadeTransition(
                                javafx.util.Duration.millis(200), view);
                ft.setFromValue(0.4);
                ft.setToValue(1.0);
                stage.toFront();
                stage.requestFocus();
                ft.play();

            })
        );

        // LAYOUT FINAL :

        view.setTop(toolbar);
        view.setLeft(leftPanel);
        view.setCenter(centerPanel);
        view.setRight(scenePanel.getView());
        view.setStyle("-fx-background-radius: " + BG_BASE + ";");

        BorderPane.setMargin(leftPanel, new Insets(0));
        BorderPane.setMargin(centerPanel, new Insets(0));
        BorderPane.setMargin(scenePanel.getView(), new Insets(0));

        leftPanel.setMaxHeight(Double.MAX_VALUE);
        centerPanel.setMaxWidth(Double.MAX_VALUE);
        centerPanel.setMaxHeight(Double.MAX_VALUE);
        scenePanel.getView().setMaxHeight(Double.MAX_VALUE);

        }



    public Slider createSlider(){
        Slider slider = new Slider(0, 255, 0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(64);
        //slider.setPrefWidth(400);
        slider.setMaxWidth(Double.MAX_VALUE); ////////////////////////////////////
        VBox.setVgrow(slider, Priority.NEVER); ///////////////////////////////////
        return slider;
    }



    public BorderPane getView()
    {
        return view;

    }

    public void updateSliders(Fixture fixture){
        if(fixture == null) return;

        //Deshabilitar listeners para evitar loops
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

        updatePreview();


    }

    private void updatePreview(){
        int r = (int)sliderR.getValue();
        int g = (int)sliderG.getValue();
        int b = (int)sliderB.getValue();
        int y = (int)sliderY.getValue();
        int w = (int)sliderW.getValue();
        double dimmerFactor = (int)(sliderDimmer.getValue() / 255.0);


        int previewR = (int)(Math.min(255, r+y) * dimmerFactor);
        int previewG = (int)(Math.min(255, g + (y/2) + w) * dimmerFactor);
        int previewB = (int)(Math.min(255, b + w) * dimmerFactor);

        colorPreview.setFill(Color.rgb(previewR, previewG, previewB));

    }

    public Fixture getSelectedFixture(){
        return selectedFixture;

    }





    private Slider createFaderSlider(){

    Slider slider = new Slider(0, 255, 0);
    slider.setOrientation(Orientation.VERTICAL);
    slider.setPrefHeight(220);
    slider.setMaxHeight(Double.MAX_VALUE);
    slider.setShowTickMarks(true);
    slider.setShowTickLabels(false);
    slider.setMajorTickUnit(64);
    return slider;
     }

     private VBox buildFaderColumn(String name, Slider slider, String color) {
         Label valueLabel = new Label("000");
         valueLabel.setStyle(
                 "-fx-font-family: 'Courier New';" +
                         "-fx-font-size: 11px;" +
                         "-fx-text-fill: " + color + ";" +
                         "-fx-font-weight: bold;"
         );

         // ACTUALIZA EL LABEL AL DESLIZAR EL SLIDER
         slider.valueProperty().addListener((o, ov, nv) ->
                 valueLabel.setText(String.format("%03d", nv.intValue()))
         );

         // TRACK DE COLOR DEL FADER
         slider.setStyle(
                 "-fx-control-inner-background: #1a1a2a;" +
                         "-fx-accent: " + color + ";"
         );

         Label nameLabel = new Label(name);
         nameLabel.setStyle("-fx-font-family: 'Courier New';" +
                 "-fx-font-size: 10px;" +
                 "-fx-text-fill: " + color + ";" +
                 "-fx-font-weight: bold;"
         );

         VBox col = new VBox(6);
         col.setAlignment(Pos.BOTTOM_CENTER);
         col.setStyle(
                 "-fx-background-color: " + BG_CARD + ";" +
                         "-fx-background-radius: 4;" +
                         "-fx-padding: 10 12;"
         );
         col.setMinWidth(60);
         HBox.setHgrow(col, Priority.ALWAYS);
         col.getChildren().addAll(valueLabel, slider, nameLabel);
         return col;
     }

     private Separator buildSeparator() {
    Separator sep = new Separator(Orientation.VERTICAL);
    sep.setStyle("-fx-background-color: " + BG_ELEVATED + ";");
    sep.setPrefHeight(Double.MAX_VALUE);
    return sep;
     }

}


