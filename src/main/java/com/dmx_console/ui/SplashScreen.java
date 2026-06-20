package com.dmx_console.ui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;

public class SplashScreen {

    private final Stage splashStage;
    private final Runnable onFinished;

    public SplashScreen(Runnable onFinished){
        this.onFinished = onFinished;
        this.splashStage = new Stage();
        build();
    }

    private void build(){
        StackPane root = new StackPane();
        root.setPrefSize(900, 660);
        //root.setPrefSize(600, 360);
        root.setStyle(
                "-fx-background-color: #00020a;" +
                "-fx-border-color: #0a2a4a;" +
                        "-fx-border-width: 1;"
        );

        Canvas gridCanvas = new Canvas(900, 660);
        drawHoloGrid(gridCanvas.getGraphicsContext2D());

        Canvas empireCanvas = new Canvas(900, 660);
        GraphicsContext egc = empireCanvas.getGraphicsContext2D();
        egc.setStroke(Color.web("#0a2a4a"));
        egc.setLineWidth(1);
        egc.strokeOval(220,80,160,160);
        egc.strokeOval(240,100,120,120);
        egc.setStroke(Color.web("#44aaff22"));
        egc.strokeOval(200,60,200,200);

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("jDMX CONSOLE");
        title.setStyle(
                "-fx-font-family: 'Orbitron'; " +
                        "-fx-font-size: 36px;"  +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #44aaff;"
        );

        Label subtitle = new Label("Open-source Lightning Control System");
        subtitle.setStyle(
                "-fx-font-family: 'VT323';" +
                        "-fx-font-size: 10px;" +
                        "-fx-text-fill: #2a5a8a;" +
                        "-fx-letter-spacing: 3;"
        );

        Label version = new Label("v1.0.0");
        version.setStyle(
                "-fx-font-family: 'VT323';" +
                        "-fx-font-size: 10px;" +
                        "-fx-text-fill: #1a3a5a;"
        );

        StackPane progressContainer = new StackPane();
        //progressContainer.setPrefSize(400,4);
        progressContainer.setPrefSize(800,5);

        Rectangle progressBg = new Rectangle(800, 5);
        progressBg.setFill(Color.web("#0a1a2a"));
        progressBg.setArcWidth(2);
        progressBg.setArcHeight(2);

        //Rectangle progressFill = new Rectangle(0,4);
        Rectangle progressFill = new Rectangle(800,5);
        progressFill.setFill(Color.web("#44aaff"));
        progressFill.setArcWidth(2);
        progressFill.setArcHeight(2);
        progressFill.setEffect(new DropShadow(
                BlurType.GAUSSIAN, Color.web("#44aaff"), 8,0.6,0,0
        ));

        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);
        progressContainer.getChildren().addAll(progressBg, progressFill);

        Label statusLabel = new Label("INITIALIZING SYSTEMS...");
        statusLabel.setStyle(
                "-fx-font-family: 'Share Tech Mono';" +
                        "-fx-font-size: 10px;"+
                        "-fx-text-fill: #2a5a8a;"
        );

        content.getChildren().addAll(
                title, subtitle, version,
                progressContainer, statusLabel
        );

        root.getChildren().addAll(gridCanvas, empireCanvas, content);


        //Scene scene = new Scene(root, 600, 360);
        Scene scene = new Scene(root, 900, 660);
        scene.setFill(Color.TRANSPARENT);

        try {
            scene.getStylesheets().add(
                    getClass().getResource(
                            "/styles.css").toExternalForm()
            );
        } catch (Exception ignored){}

        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setScene(scene);
        splashStage.centerOnScreen();

        animateSplash(title, progressFill, statusLabel);
        }

        private void animateSplash(Label title,
                                   Rectangle progressFill,
                                   Label statusLabel){

        String[] messages = {
                "INITIALIZING DMX ENGINE...",
                "LOADING FIXTURE PROFILES...",
                "CALIBRATING UNIVERSE 1...",
                "CONNECTING OUTPUT INTERFACE...",

        };

            Timeline titlePulse = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(title.effectProperty(),
                                    new DropShadow(BlurType.GAUSSIAN,
                                            Color.web("#44aaff"),30,0.9,0,0))
                    )
            );
            titlePulse.setAutoReverse(true);
            titlePulse.setCycleCount(Animation.INDEFINITE);
            titlePulse.play();

            Timeline progress = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(progressFill.widthProperty(),0)),
                    new KeyFrame(Duration.millis(8000),
                    new KeyValue(progressFill.widthProperty(),400,
                            Interpolator.EASE_IN))
            );

            Timeline messages_tl = new Timeline();
            for(int i=0; i< messages.length; i++){
                final String msg = messages[i];
                messages_tl.getKeyFrames().add(
                        new KeyFrame(Duration.millis(1*600L), e ->
                                statusLabel.setText(msg))
                );
            }

            title.setOpacity(0);
            Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(title.opacityProperty(),0)),
                    new KeyFrame(Duration.millis(800),
                            new KeyValue(title.opacityProperty(),1))
            );

            Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.millis(400),
                            new KeyValue(splashStage.getScene()
                                    .getRoot().opacityProperty(),1)),
                    new KeyFrame(Duration.millis(800),
                            new KeyValue(splashStage.getScene()
                                    .getRoot().opacityProperty(),0))
            );
            fadeOut.setOnFinished(e-> {
                splashStage.close();
                onFinished.run();
            });

            SequentialTransition sequence = new SequentialTransition(
                    fadeIn,
                    new ParallelTransition(progress, messages_tl),
                    new PauseTransition(Duration.millis(300)),
                    fadeOut
            );
            sequence.play();
        }

        private void drawHoloGrid(GraphicsContext gc){
            gc.setStroke(Color.web("#0a1a2a"));
            gc.setLineWidth(0.5);

            for(int y=0; y<360; y+=30){
                gc.strokeLine(0,y,600,y);
            }
            for(int x=0; x<600; x+=30){
                gc.strokeLine(x,0,x,360);
            }

            gc.setStroke(Color.web("#0a2a4a"));
            gc.setLineWidth(1);
            gc.strokeLine(0,180,600,180);
            gc.strokeLine(300,0,300,360);
        }

        public void show(){
        splashStage.show();
        }
    }

