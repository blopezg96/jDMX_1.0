package com.dmx_console.ui;

import com.dmx_console.model.Chase;
import com.dmx_console.model.ChaseMode;
import com.dmx_console.service.ChaseEngine;
import com.dmx_console.service.SceneService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.awt.*;

public class ChasePanel {

    private final ChaseEngine engine;
    private final SceneService sceneService;
    private final VBox view;
    private Chase currentChase;
    private ListView<String> stepList;
    private Label statusLabel;
    private Rectangle stepIndicator;

    public ChasePanel(ChaseEngine engine, SceneService sceneService){
        this.engine = engine;
        this.sceneService = sceneService;
        this.view = new VBox(8);
        buildUI();
    }

    private void buildUI(){
        view.setPadding(new Insets(10));
        view.getStyleClass().add("hw-right");
        view.setPrefWidth(220);
        view.setMinWidth(200);
        view.setMaxHeight(Double.MAX_VALUE);

        // Titulo
        Label title = new Label("CHASES");
        title.getStyleClass().add("hw-panel-title");

        // Nombre del Chase
        TextField nameField = new TextField();
        nameField.setPromptText("Chase name...");
        nameField.getStyleClass().add("hw-scene-input");

        // BPM
        HBox bpmRow = new HBox(8);
        bpmRow.setAlignment(Pos.CENTER_LEFT);
        Label bpmLabel = new Label("BPM: ");
        bpmLabel.getStyleClass().add("hw-fix-meta");
        Slider bpmSlider = new Slider(20, 240, 120);
        bpmSlider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(bpmSlider, Priority.ALWAYS);
        Label bpmVal = new Label("120");
        bpmVal.getStyleClass().add("hw-fval-green");
        bpmVal.setMinWidth(30);
        bpmSlider.valueProperty().addListener((o, ov,nv) ->{
            bpmVal.setText(String.valueOf(nv.intValue()));
            if(engine.isRunning()){
                engine.setBpm(nv.doubleValue());
            }
        });
        bpmRow.getChildren().addAll(bpmLabel, bpmSlider, bpmVal);

        // Modo
        HBox modeRow = new HBox(6);
        modeRow.setAlignment(Pos.CENTER_LEFT);
        Label modelLabel = new Label("MODE: ");
        modelLabel.getStyleClass().add("hw-fix-meta");
        ComboBox<ChaseMode> modeBox = new ComboBox<>();
        modeBox.getItems().addAll(ChaseMode.values());
        modeBox.setValue(ChaseMode.FORWARD);
        modeBox.setMaxWidth(Double.MAX_VALUE);
        modeBox.setStyle(
                "-fx-background-color: #00040f;" +
                        "fx-border-color: #0a2a4a;" +
                        "fx-text-fill: #44aaff;"
        );
        HBox.setHgrow(modeBox, Priority.ALWAYS);
        modeRow.getChildren().addAll(modelLabel, modeBox);

        // Pasos de chase, escenas disponibles.
        Label stepsTitle = new Label("SCENES: ");
        stepsTitle.getStyleClass().add("hw-fix-meta");

        stepList = new ListView<>();
        stepList.getStyleClass().add("hw-scene-list");
        stepList.setPrefHeight(150);
        VBox.setVgrow(stepList, Priority.ALWAYS);
        stepList.setMaxHeight(Double.MAX_VALUE);
        refreshStepList();

        // Botones agregar/quitar step
        HBox stepBtns = new HBox(4);
        Button addStepBtn = new Button(" + Add Step");
        addStepBtn.setMaxWidth(Double.MAX_VALUE);
        addStepBtn.getStyleClass().add("hw-btn-capture");
        HBox.setHgrow(addStepBtn, Priority.ALWAYS);





    }
}
