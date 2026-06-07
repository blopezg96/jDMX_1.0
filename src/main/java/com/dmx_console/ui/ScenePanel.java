package com.dmx_console.ui;

import com.dmx_console.model.Fixture;
import com.dmx_console.model.Scene;
import com.dmx_console.service.SceneService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ScenePanel {

    private final SceneService sceneService;
    private final List<Fixture> rig;
    private final VBox view;
    private final ListView<String> sceneList;
    private final Runnable onSceneApplied;


    public ScenePanel(SceneService sceneService, List<Fixture> rig, Runnable onSceneApplied){
        this.sceneService = sceneService;
        this.rig = rig;
        this.view = new VBox(10);
        this.sceneList = new ListView<>();
        this.onSceneApplied = onSceneApplied;
        buildUI();
        refreshList();
    }
    private void buildUI(){
        view.setPadding(new Insets(10));
        view.setPrefWidth(260);
        view.setMinWidth(220); /////////////////////////////////////
        view.getStyleClass().add("hw-right");
        /*
        view.setStyle("""
                -fx-background-color: #181818;
                -fx-padding:10;
                """);
*/

        Label title = new Label("Escenas");
        title.getStyleClass().add("hw-scenes-label");
        /*
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px;"+
        "fx-font-weight: bold; ");
*/

        // Nombrar una nueva escena
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre de escena: ");
        nameField.getStyleClass().add("hw-scenes-input");

        /* nameField.setStyle("-fx-background-color: #3a3a3a;" +
                "-fx-text-fill: white;"); */

        // Boton para capturar una escena actual
        Button btnCapture = new Button("Capturar escena");
        btnCapture.setMaxWidth(Double.MAX_VALUE);
        btnCapture.getStyleClass().add("hw-btn-capture");
        /*
        btnCapture.setStyle("-fx-background-color: #e67e22;" +
                "-fx-text-fill: white; -fx-font-weight:" +
                "bold;"); */

        // Boton de apply una escena seleccionada.
        Button btnApply = new Button("Aplicar");
        btnApply.setMaxWidth(Double.MAX_VALUE);
        btnApply.getStyleClass().add("hw-btn-apply");
        /*
        btnApply.setStyle("-fx-background-color: #27ae60;" +
                "-fx-text-fill: white; -fx-font-weight: " +
                "bold;");
       */

        // Boton para eliminar una escena
        Button btnDelete = new Button("Eliminar escena");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.getStyleClass().add("hw-btn-delete");
        /*
        btnDelete.setStyle("-fx-background-color: #c0392b;" +
                "-fx-text-fill: white; -fx-font-weight: " +
                "bold;");
*/

        sceneList.getStyleClass().add("hw-scene-list");
        /*
        sceneList.setStyle("-fx-background-color: #3a3a3a;" +
                "-fx-text-fill: white;"); */
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
            refreshList();
            System.out.println("[UI] Escena eliminada: " + selected.getName());
        });

        view.getChildren().addAll(
                title,
                nameField,
                btnCapture,
                new Separator(),
                new Label("Escenas guardadas: "),
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
