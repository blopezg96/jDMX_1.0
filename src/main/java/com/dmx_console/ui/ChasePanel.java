package com.dmx_console.ui;

import com.dmx_console.model.Chase;
import com.dmx_console.model.ChaseMode;
import com.dmx_console.model.Scene;
import com.dmx_console.service.ChaseEngine;
import com.dmx_console.service.SceneService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

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

    private void buildUI() {
        view.setPadding(new Insets(10));
        view.getStyleClass().add("hw-right");
        view.setPrefWidth(220);
        view.setMinWidth(200);
        view.setMaxHeight(Double.MAX_VALUE);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("CHASES");
        title.getStyleClass().add("hw-panel-title");

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);

        Button btnRefresh = new Button("o");
        btnRefresh.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #44aaff;" +
                        "-fx-font-size: 12px;" +
                        "-fx-cursor: hand;"
        );
        btnRefresh.setOnAction(e -> refreshStepList());
        titleRow.getChildren().addAll(title, titleSpacer, btnRefresh);

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
        bpmSlider.valueProperty().addListener((o, ov, nv) -> {
            bpmVal.setText(String.valueOf(nv.intValue()));
            if (engine.isRunning()) {
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
        view.setOnMouseEntered(e -> refreshStepList());

        refreshStepList();

        // Botones agregar/quitar step
        HBox stepBtns = new HBox(4);
        Button addStepBtn = new Button(" + Add Step");
        addStepBtn.setMaxWidth(Double.MAX_VALUE);
        addStepBtn.getStyleClass().add("hw-btn-capture");
        HBox.setHgrow(addStepBtn, Priority.ALWAYS);

        Button removeStepBtn = new Button(" - Remove Step");
        removeStepBtn.setMaxWidth(Double.MAX_VALUE);
        removeStepBtn.getStyleClass().add("hw-btn-delete");
        HBox.setHgrow(removeStepBtn, Priority.ALWAYS);
        stepBtns.getChildren().addAll(addStepBtn, removeStepBtn);

        // Indicadpr de paso Actvio
        HBox statusRow = new HBox(8);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        stepIndicator = new Rectangle(10, 10);
        stepIndicator.setFill(Color.web("#1a3a5a"));
        stepIndicator.setArcWidth(3);
        stepIndicator.setArcHeight(3);
        statusLabel = new Label("DETENIDO");
        statusLabel.getStyleClass().add("hw-fix-meta");
        statusRow.getChildren().addAll(stepIndicator, statusLabel);


        // Botones de control

        Button btnPlay = new Button("PLAY");
        btnPlay.setMaxWidth(Double.MAX_VALUE);
        btnPlay.getStyleClass().add("hw-btn-apply");

        Button btnPause = new Button("PAUSE");
        btnPause.setMaxWidth(Double.MAX_VALUE);
        btnPause.getStyleClass().add("hw-btn-capture");

        Button btnStop = new Button("STOP");
        btnStop.setMaxWidth(Double.MAX_VALUE);
        btnPause.getStyleClass().add("hw-btn-blackout-fix");

        HBox controlBtns = new HBox(4, btnPlay, btnPause, btnStop);
        controlBtns.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnPlay, Priority.ALWAYS);
        HBox.setHgrow(btnPause, Priority.ALWAYS);
        HBox.setHgrow(btnStop, Priority.ALWAYS);


        // Agregar paso al chase
        addStepBtn.setOnAction(e -> {
            int index = stepList.getSelectionModel().getSelectedIndex();
            if (index < 0) {
                shownAlert("Select a scene to add...");
                return;
            }
            if (currentChase == null) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    shownAlert("Write a name for the current chase. ");
                    return;
                }
                currentChase = new Chase(name,
                        bpmSlider.getValue(), modeBox.getValue());
            }

            Scene selected = sceneService.getScenes().get(index);
            currentChase.addStep(selected);
            System.out.println("[CHASE] Paso agregado: " + selected.getName());
            refreshStepList();
        });

        // Eliminar paso

        removeStepBtn.setOnAction(e -> {
            int index = stepList.getSelectionModel().getSelectedIndex();
            if (currentChase != null && index >= 0) {
                currentChase.removeStep(index);
                refreshStepList();
            }
        });

        // Play
        btnPlay.setOnAction(e -> {
            if (currentChase == null || currentChase.getSteps().isEmpty()) {
                shownAlert("Add steps to chase before continue");
                return;
            }
            currentChase.setMode(modeBox.getValue());
            currentChase.setBpm(bpmSlider.getValue());
            engine.play(currentChase);
            statusLabel.setText("PLAYING - " + currentChase.getName());
            statusLabel.setStyle("-fx-text-fill: #3aff6a;");
            stepIndicator.setFill(Color.web("#3aff6a"));

        });

        // Pause
        btnPause.setOnAction(e -> {
            engine.pause();
            statusLabel.setText("PAUSED. ");
            statusLabel.setStyle("-fx-text-fill: #ffe53a;");
            stepIndicator.setFill(Color.web("#ffe53"));
        });

        //Stop
        btnStop.setOnAction(e -> {
            engine.stop();
            statusLabel.setText("STOPED");
            statusLabel.setStyle("-fx-text-fill: #2a5a8a;");
            stepIndicator.setFill(Color.web("#1a3a5a"));
        });

        // Callback cuando cambia el paso
        engine.setOnStepChanged(step -> {
            if (currentChase != null && step < currentChase.getSteps().size()) {
                String sceneName = currentChase.getSteps().get(step).getName();
                statusLabel.setText("[STEP]" + (step + 1) +
                        "--" + sceneName);
            }
        });

        view.getChildren().addAll(
                titleRow,
                nameField,
                bpmRow,
                new Separator(),
                stepsTitle,
                stepList,
                stepBtns,
                new Separator(),
                statusRow,
                controlBtns
        );
    }
        public void refreshStepList(){
            stepList.getItems().clear();

            List<Scene> scenes = sceneService.getScenes();
            for (int i=0; i<scenes.size(); i++){
                String prefix = (currentChase != null && currentChase.getSteps().contains(scenes.get(i)))
                        ? "ok" : " ";
                stepList.getItems().add(prefix + scenes.get(i).getName());
            }


        }

        private void shownAlert(String msg){
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("jDMX");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
            });
        }

        public VBox getView(){
            return view;
        }


    }



