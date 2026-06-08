package com.dmx_console.ui;

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.service.SceneService;
import javafx.animation.Animation;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.dmx_console.service.FixtureService;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

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
    private javafx.animation.Timeline strobeTimeLine;
    private Slider sliderR;
    private Slider sliderG;
    private Slider sliderB;
    private Slider sliderY;
    private Slider sliderW;
    private Slider sliderStrobe;
    private Slider sliderDimmer;
    private HBox faderBank;
    private int[] savedValues = new int[7];
    private final ListView<Fixture> fixtureList = new ListView<>();



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
        toolbar.getStyleClass().add("hw-toolbar");


        VBox stat512 = buildStat("512", "CHANNELS");
        VBox statLive = buildStatGreen("LIVE", "STATUS");
        VBox statU = buildStat("U-1", "UNIVERSE");

        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setStyle("-fx-background-color: #0a2a4a");
        Separator sep2 = new Separator(Orientation.VERTICAL);
        sep2.setStyle("-fx-background-color: #0a2a4a");
        Separator sep3 = new Separator(Orientation.VERTICAL);
        sep3.setStyle("-fx-background-color: #0a2a4a");


        Label appTitle = new Label("j D M X");
        appTitle.getStyleClass().add("hw-title");



        Label appSubtitle = new Label("CONSOLE v1.0");
        appSubtitle.getStyleClass().add("hw-subtitle");


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // INDICADOR DE DMX UNIVERSE
        Label universeLabel = new Label("[] UNIVERSE 1");
        universeLabel.getStyleClass().add("hw-stat-lbl");


        /// Boton de blackout general
        Button blackOutAll = new Button("xxx BLACKOUT ALL xxx");
        blackOutAll.getStyleClass().add("hw-btn-blackout-all");



        blackOutAll.setOnAction(e -> {
            if (strobeTimeLine != null){
                strobeTimeLine.stop();
            }
            service.blackoutAll();
            updateSliders(selectedFixture);
            updatePreview();
        });

        toolbar.getChildren().addAll(
                appTitle, appSubtitle, spacer,
                stat512, sep1, statLive, sep2, statU, sep3,
                blackOutAll
        );


        // Modelar panel izquierdo - Lista de Fixtures

         VBox leftPanel = new VBox(0);
         leftPanel.setMinWidth(170);
         leftPanel.setMaxWidth(200);
         leftPanel.getStyleClass().add("hw-left");


         Label fixturesTitle = new Label("FIXTURES");
         fixturesTitle.getStyleClass().add("hw-panel-title");




        fixtureList.getItems().addAll(rig);
        fixtureList.getStyleClass().add("hw-fixture-cell");



        VBox.setVgrow(fixtureList, Priority.ALWAYS);

        fixtureList.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Fixture item, boolean empty){
                super.updateItem(item, empty);
                if(empty || item ==null){
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-border-color: transparent; ");
                } else {
                    HBox cell = new HBox(8);
                    cell.setAlignment(Pos.CENTER_LEFT);

                    /// ICONO COLORIDO DE FIXTURE

                javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(5);
                int r = service.getChannelValue(item, ChannelFunction.RED);
                int g = service.getChannelValue(item, ChannelFunction.GREEN);
                int b = service.getChannelValue(item, ChannelFunction.BLUE);
                int dim = service.getChannelValue(item, ChannelFunction.DIMMER);
                double f = dim/255.0;

                Color dotColor = (r+g+b == 0)
                        ? Color.web("#1a3a5a")
                        : Color.rgb((int)(r*f), (int)(g*f), (int)(b*f));

                dot.setFill(dotColor);
                dot.setId("dot_" + item.getName());
                dot.setEffect(new javafx.scene.effect.DropShadow(
                        6, dotColor
                ));

                Label name = new Label(item.getName());
                name.setStyle(
                        "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 12px;" +
                                "-fx-text-fill: " + TEXT_PRIMARY + ";"
                );

                // direccion en la que se encuentra el fixture
                Label addr = new Label("@" + item.getAddress());
                addr.getStyleClass().add("hw-fix-addr");


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
        centerPanel.getStyleClass().add("hw-center");

        VBox.setVgrow(centerPanel, Priority.ALWAYS);

        // Header fixture seleccionado}
        HBox fixtureHeader = new HBox(12);
        fixtureHeader.setPadding(new Insets(12,16,12,16));
        fixtureHeader.setAlignment(Pos.CENTER_LEFT);
        fixtureHeader.getStyleClass().add("hw-fix-header");


        Label labelFixture = new Label("-SELECT A FIXTURE-");
        labelFixture.getStyleClass().add("hw-fix-label");


        Label labelAddr = new Label("");
        labelAddr.getStyleClass().add("hw-fix-meta");


        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setStyle("-fx-color-label-visible: false;");

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
                buildFaderColumn("DIM", sliderDimmer, "dim"),
                buildSeparator(),
                buildFaderColumn("RED", sliderR, "red"),
                buildSeparator(),
                buildFaderColumn("GRN", sliderG, "green"),
                buildSeparator(),
                buildFaderColumn("BLU", sliderB, "blue"),
                buildSeparator(),
                buildFaderColumn("YLW", sliderY, "yellow"),
                buildSeparator(),
                buildFaderColumn("WHT", sliderW, "white"),
                buildSeparator(),
                buildFaderColumn("STRB", sliderStrobe, "strobe")
        );

        /// Button blackOut fixture

        Button btnBlackout = new Button("BLACKOUT FIXTURE");
        btnBlackout.setMaxWidth(Double.MAX_VALUE);
        btnBlackout.getStyleClass().add("hw-btn-blackout-fix");



        btnBlackout.setOnMouseEntered(e -> {
            btnBlackout.getStyleClass().add("hw-btn-blackout-fix:hover");
        });

        btnBlackout.setOnMouseExited(e -> {
            btnBlackout.getStyleClass().add("hw-btn-blackout-fix");
        });

        Button btnBump = new Button("HOLD BLACKOUT");
        btnBump.setMaxWidth(Double.MAX_VALUE);
        btnBump.getStyleClass().add("hw-btn-hold");


        btnBump.setOnMouseEntered(e ->{
            btnBump.getStyleClass().add("hw-btn-hold:hover");
        });

        btnBump.setOnMouseExited(e -> {
            btnBump.getStyleClass().add("hw-btn-hold");
        });

        btnBump.setOnMousePressed(e -> {
            if(selectedFixture == null) return;

            savedValues[0] = (int)  sliderDimmer.getValue();
            savedValues[1] = (int)  sliderR.getValue();
            savedValues[2] = (int)  sliderG.getValue();
            savedValues[3] = (int)  sliderB.getValue();
            savedValues[4] = (int)  sliderW.getValue();
            savedValues[5] = (int)  sliderY.getValue();
            savedValues[6] = (int)  sliderStrobe.getValue();


            if(strobeTimeLine != null){
                strobeTimeLine.pause();
            }
            service.blackout(selectedFixture);

            btnBump.getStyleClass().add("hw-btn-hold:selected");
            colorPreview.setFill(Color.BLACK);
        });

        btnBump.setOnMouseReleased(e -> {
            if(selectedFixture == null) return;

            service.setChannel(selectedFixture, ChannelFunction.DIMMER, savedValues[0]);
            service.setChannel(selectedFixture, ChannelFunction.RED, savedValues[1]);
            service.setChannel(selectedFixture, ChannelFunction.GREEN, savedValues[2]);
            service.setChannel(selectedFixture, ChannelFunction.BLUE, savedValues[3]);
            service.setChannel(selectedFixture, ChannelFunction.WHITE, savedValues[4]);
            service.setChannel(selectedFixture, ChannelFunction.YELLOW, savedValues[5]);
            service.setChannel(selectedFixture, ChannelFunction.STROBE, savedValues[6]);

            if (strobeTimeLine != null){
                strobeTimeLine.play();
            }

            btnBump.getStyleClass().add("hw-btn-hold");
            updatePreview();
            updateStrobe();
        });

        centerPanel.getChildren().addAll(
                fixtureHeader,
                colorPreview,
                btnBlackout,
                btnBump,
                fadersRow

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
                    sliderDimmer.setValue(255);

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

            updateFixtureDot(selectedFixture);

            updatePreview();
            updateStrobe();
            fixtureList.refresh();

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

        colorPicker.setOnAction(e -> {
            Color c = colorPicker.getValue();
            int r = (int) (c.getRed() * 255);
            int g = (int) (c.getGreen()* 255);
            int b = (int) (c.getBlue() * 255);

            sliderR.valueProperty().removeListener(dmxListener);
            sliderG.valueProperty().removeListener(dmxListener);
            sliderB.valueProperty().removeListener(dmxListener);
            sliderR.setValue(r);
            sliderG.setValue(g);
            sliderB.setValue(b);
            sliderR.valueProperty().addListener(dmxListener);
            sliderG.valueProperty().addListener(dmxListener);
            sliderB.valueProperty().addListener(dmxListener);

            if(selectedFixture != null ){
                service.setColor(selectedFixture, r ,g, b);
                updatePreview();
                updateFixtureDot(selectedFixture);
            }

        });

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
        view.setStyle("-fx-background-color: " + BG_BASE + ";");

        BorderPane.setMargin(leftPanel, new Insets(0));
        BorderPane.setMargin(centerPanel, new Insets(0));
        BorderPane.setMargin(scenePanel.getView(), new Insets(0));

        leftPanel.setMaxHeight(Double.MAX_VALUE);
        centerPanel.setMaxWidth(Double.MAX_VALUE);
        centerPanel.setMaxHeight(Double.MAX_VALUE);
        scenePanel.getView().setMaxHeight(Double.MAX_VALUE);
        scenePanel.getView().getStyleClass().add("hw-right");

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
        updateStrobe();


    }

    private void updatePreview(){
        int r = (int)sliderR.getValue();
        int g = (int)sliderG.getValue();
        int b = (int)sliderB.getValue();
        int y = (int)sliderY.getValue();
        int w = (int)sliderW.getValue();

        double dimmerFactor = (sliderDimmer.getValue() / 255.0); /// muestra progresivamente el color segun el dimmer


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
    slider.setPrefHeight(320);
    slider.setMaxHeight(Double.MAX_VALUE);
    slider.setShowTickMarks(true);
    slider.setShowTickLabels(false);
    slider.setMajorTickUnit(64);
    return slider;
     }

     private VBox buildFaderColumn(String name, Slider slider, String colorClass) {
         Label valueLabel = new Label("000");
         valueLabel.getStyleClass().addAll("hw-fval", "hw-fval-" + colorClass);


         // ACTUALIZA EL LABEL AL DESLIZAR EL SLIDER
         slider.valueProperty().addListener((o, ov, nv) ->
                 valueLabel.setText(String.format("%03d", nv.intValue()))
         );

         // TRACK DE COLOR DEL FADER
         slider.getStyleClass().add("slider-" + colorClass);
         slider.valueProperty().addListener((o, ov, nv) ->
                 valueLabel.setText(String.format("%03d", nv.intValue()))
         );




         Label nameLabel = new Label(name);
         nameLabel.getStyleClass().addAll("hw-flabel", "hw-flabel-" +
                 colorClass);


         VBox col = new VBox(6);
         col.setAlignment(Pos.BOTTOM_CENTER);
         col.getStyleClass().add("hw-fader-col");


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

     private void updateStrobe(){
        int strobeValue = (int) sliderStrobe.getValue();

        if(strobeTimeLine != null){
            strobeTimeLine.stop();
            strobeTimeLine = null;
        }
        if(strobeValue == 0){
            updatePreview();
            return;
        }

        double fps = 1 + (strobeValue/255.0) * 19;
        double intervalMs = 1000.0 / fps;

        strobeTimeLine = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(intervalMs),
                        e -> {
                            if(colorPreview.getFill().equals(Color.BLACK)){
                                updatePreview();
                            } else {
                                colorPreview.setFill(Color.BLACK);
                            }
                        }
                )
        );
        strobeTimeLine.setCycleCount(Animation.INDEFINITE);
        strobeTimeLine.play();


     }

     private VBox buildStat(String value, String label){
        Label val = new Label(value);
        val.getStyleClass().add("hw-stat-val");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("hw-stat-lbl");
        VBox box = new VBox(2, val, lbl);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(0, 12, 0, 12));
        return box;
     }

     private VBox buildStatGreen(String value, String label){
        Label val = new Label(value);
        val.getStyleClass().add("hw-stat-val-green");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("hw-stat-lbl");
        VBox box = new VBox(2, val, lbl);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(0, 12, 0, 12));

        return box;
     }


    private void updateFixtureDot(Fixture fixture){

        int r = service.getChannelValue(fixture, ChannelFunction.RED);
        int g = service.getChannelValue(fixture, ChannelFunction.GREEN);
        int b = service.getChannelValue(fixture, ChannelFunction.BLUE);
        int dimmer = service.getChannelValue(fixture, ChannelFunction.DIMMER);
        double factor = dimmer /255.0;

        Color dotColor = Color.rgb(
                (int)(r * factor),
                (int)(g * factor),
                (int)(b * factor)
        );

        fixtureList.lookupAll(".list-cell").forEach(node -> {

        });
        fixtureList.refresh();

    }


}


