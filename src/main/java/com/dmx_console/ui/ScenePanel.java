package com.dmx_console.ui;

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.model.Scene;
import com.dmx_console.service.SceneService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

public class ScenePanel {

    private final SceneService sceneService;
    private final List<Fixture> rig;
    private final VBox view;
    private final ListView<String> sceneList;
    private final Runnable onSceneApplied;
    private final FixturePreviewGrid fixturePreviewGrid;



    public ScenePanel(SceneService sceneService, List<Fixture> rig,
                      Runnable onSceneApplied, FixturePreviewGrid previewGrid){
        this.sceneService = sceneService;
        this.rig = rig;
        this.view = new VBox(10);
        this.sceneList = new ListView<>();
        this.onSceneApplied = onSceneApplied;
        this.fixturePreviewGrid = previewGrid;
        buildUI();
        refreshList();
    }
    private void buildUI(){
        view.setPadding(new Insets(10));
        view.setPrefWidth(260);
        view.setMinWidth(220); /////////////////////////////////////
        view.getStyleClass().add("hw-right");



        Label title = new Label("ESCENES");
        title.getStyleClass().add("hw-scenes-label");

        // Nombrar una nueva escena
        TextField nameField = new TextField();
        nameField.setPromptText("SCENE NAME: ");
        nameField.getStyleClass().add("hw-scenes-input");



        // Boton para capturar una escena actual
        Button btnCapture = new Button("RECORD ESCENE");
        btnCapture.setMaxWidth(Double.MAX_VALUE);
        btnCapture.getStyleClass().add("hw-btn-capture");


        // Boton de apply una escena seleccionada.
        Button btnApply = new Button("APPLY ESCENE");
        btnApply.setMaxWidth(Double.MAX_VALUE);
        btnApply.getStyleClass().add("hw-btn-apply");


        // Boton para eliminar una escena
        Button btnDelete = new Button("DELETE ESCENE");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.getStyleClass().add("hw-btn-delete");


        sceneList.getStyleClass().add("hw-scene-list");

        sceneList.setPrefHeight(200);

        // LOGICA DE LA UI

        // CAPTURAR ESTADO ACTUAL COMO UNA ESCENA
        btnCapture.setOnAction(e -> {
            String name = nameField.getText().trim();
            if(name.isEmpty()){
                shownAlert("Escribe un nombre para la escena: ");
                return;
            }
            sceneService.capture(name, rig);
            refreshList();
            nameField.clear();
            System.out.println("[UI] Escena capturada: " + name);
        });

        // Aplicar escena seleccionada
        btnApply.setOnAction(e -> {
            int index = sceneList.getSelectionModel().getSelectedIndex();
            if(index < 0){
                shownAlert("Selecciona una escena. ");
                return;
            }
            Scene selected = sceneService.getScenes().get(index);
            sceneService.apply(selected, rig);
            fixturePreviewGrid.refresh();   // escenas actualizan el preview Grid
            onSceneApplied.run();

            System.out.println("[UI] Escena aplicada: " +selected.getName());
        });

        // Eliminar escena seleccionada
        btnDelete.setOnAction(e -> {
            int index = sceneList.getSelectionModel().getSelectedIndex();
            if(index < 0){
                shownAlert("Selecciona una escena a eliminar. ");
                return;
            }
            Scene selected = sceneService.getScenes().get(index);
            sceneService.delete(selected.getName());
            fixturePreviewGrid.restart();   // escena detenida actualiza el preview grid
            refreshList();
            System.out.println("[UI] Escena eliminada: " + selected.getName());
        });

        view.getChildren().addAll(
                title,
                nameField,
                btnCapture,
                new Separator(),
                new Label("Stored Scenes: "),
                sceneList,
                btnApply,
                btnDelete
        );

        VBox.setVgrow(sceneList, Priority.ALWAYS);
        sceneList.setMaxHeight(Double.MAX_VALUE);
        view.setMaxHeight(Double.MAX_VALUE);

    }

    // Actualizar la lista con las escenas nuevas
    private void refreshList(){
        sceneList.getItems().clear();
        for(Scene s : sceneService.getScenes()){
            sceneList.getItems().add(s.getName());
        }
    }

    private void shownAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("jDMX");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView(){
        return view;
    }






}
