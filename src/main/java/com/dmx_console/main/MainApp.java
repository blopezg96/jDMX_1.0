package com.dmx_console.main;

import com.dmx_console.dmx.DmxLoop;
import com.dmx_console.model.Fixture;
import com.dmx_console.dmx.Universe;
import com.dmx_console.service.ChaseEngine;
import com.dmx_console.ui.SplashScreen;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.dmx_console.output.DMXOutput;
import com.dmx_console.output.SimulatedDMXOutput;
import com.dmx_console.service.FixtureService;
import com.dmx_console.setup.ShowSetup;
import com.dmx_console.ui.MainController;
import javafx.util.Duration;

import java.awt.*;
import java.util.List;

import static javax.print.attribute.standard.MediaSizeName.C;

public class MainApp extends Application {

    public ChaseEngine chaseEngine;

    @Override
    public void start(Stage stage) {

        Font f = Font.loadFont(
                getClass().getResourceAsStream(
                        "/fonts/Orbitron-Bold.ttf"), 12);
        Font f1 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/ShareTechMono-Regular.ttf"), 12);
        Font f2 = Font.loadFont(getClass().getResourceAsStream("/fonts/VT323-Regular.ttf"), 12);

        com.dmx_console.ui.SplashScreen splash = new SplashScreen(() ->
                Platform.runLater(() -> launchApp(stage)));
        splash.show();
    }

        /*
        // --- BACKEND ---
        Universe universe = new Universe();
        DMXOutput output = new SimulatedDMXOutput();
        FixtureService service = new FixtureService(universe, output);

        List<Fixture> rig = ShowSetup.buildRig();
        output.connect();

        // --- UI ----
        MainController controller = new MainController(rig, service);

        BorderPane root = controller.getView();
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
*/
        /*
        javafx.scene.image.Image cursorImage = new Image(getClass().getResourceAsStream(
                "/cursor/lightsaber.png"));
        scene.setCursor(new ImageCursor(cursorImage, 2, cursorImage.getHeight() / 2));
*/
        /*
        Canvas cursorCanvas = new Canvas(32,32);
        GraphicsContext gc = cursorCanvas.getGraphicsContext2D();

        gc.setStroke(Color.web("#44aaff"));
        gc.setLineWidth(1.5);
        gc.setEffect(new DropShadow(
                4, Color.web("#44aaff")
        ));

        gc.strokeLine(0,16,12,16);
        gc.strokeLine(20,16,32,16);

        gc.strokeLine(16,0,16,12);
        gc.strokeLine(16,20,16,32);

        gc.strokeOval(12,12,8,8);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage cursorImage = cursorCanvas.snapshot(params, null);
        scene.setCursor(new ImageCursor(cursorImage, 16, 16 ));

        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.setTitle("jDMX Console");
        stage.show();

        Label titleLabel = controller.getTitleLabel();


        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#44aaff"));
        glow.setBlurType(BlurType.GAUSSIAN);

        titleLabel.setEffect(glow);

        Timeline pulse = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(glow.radiusProperty(), 9),
                        new KeyValue(glow.spreadProperty(), 0.2)
                ),
                new KeyFrame(
                        Duration.seconds(1.7),
                        new KeyValue(glow.radiusProperty(), 15),
                        new KeyValue(glow.spreadProperty(), 0.5)
                )
        );

        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
        */


    private void launchApp(Stage stage){
        Universe universe = new Universe();
        DMXOutput output = new SimulatedDMXOutput();
        FixtureService service = new FixtureService(universe, output);

        List<Fixture> rig = ShowSetup.buildRig();


        DmxLoop dmxLoop = new DmxLoop(universe, output);
        dmxLoop.start();

        MainController controller = new MainController(rig, service);

        BorderPane root = controller.getView();
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        Canvas cursorCanvas = new Canvas(32,32);
        GraphicsContext gc = cursorCanvas.getGraphicsContext2D();

        gc.setStroke(Color.web("#44aaff"));
        gc.setLineWidth(1.5);
        gc.setEffect(new DropShadow(
                4, Color.web("#44aaff")
        ));

        gc.strokeLine(0,16,12,16);
        gc.strokeLine(20,16,32,16);

        gc.strokeLine(16,0,16,12);
        gc.strokeLine(16,20,16,32);

        gc.strokeOval(12,12,8,8);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage cursorImage = cursorCanvas.snapshot(params, null);
        scene.setCursor(new ImageCursor(cursorImage, 16, 16 ));

        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.setTitle("jDMX Console");
        stage.show();

        Label titleLabel = controller.getTitleLabel();


        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#44aaff"));
        glow.setBlurType(BlurType.GAUSSIAN);

        titleLabel.setEffect(glow);

        Timeline pulse = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(glow.radiusProperty(), 9),
                        new KeyValue(glow.spreadProperty(), 0.2)
                ),
                new KeyFrame(
                        Duration.seconds(1.7),
                        new KeyValue(glow.radiusProperty(), 15),
                        new KeyValue(glow.spreadProperty(), 0.5)
                )
        );

        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        root.setOpacity(0);

        stage.setOnCloseRequest(e -> {
            System.out.println("[APP] Cerrando - deteniendo DMX loop...");
            dmxLoop.stop();
            controller.getChaseEngine().stop();
            controller.stopUiRefreshLoop();
            Platform.exit();
            System.exit(0);

        });
        stage.show();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(root.opacityProperty(),0)),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(root.opacityProperty(),1))
        );
        fadeIn.play();

        stage.setOnCloseRequest(e -> {
            System.out.println("[APP] Cerrando aplicación...");
            dmxLoop.stop();
            controller.getChaseEngine().stop();

            javafx.application.Platform.exit();
            System.exit(0); // ← fuerza el cierre de la JVM completa
        });

    }

    public static void main(String[] args){
        launch(args);
    }
}
