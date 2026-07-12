package com.dmx_console.ui;

import com.dmx_console.dmx.Universe;
import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.service.FixtureService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.SystemColor.text;

public class FixturePreviewGrid {
    private final List<Fixture> rig;
    private final FixtureService service;
    private final Universe universe;
    private final VBox view;
    //private final FlowPane grid;

    private final Map<Fixture, Shape> swatches = new HashMap<>();
    private final Map<Fixture, Circle> sourceDots = new HashMap<>();

    private Label statActive;
    private Label statChase;
    private Label statManual;
    private Label statDark;


    public FixturePreviewGrid(List<Fixture> rig, FixtureService service, Universe universe) {
        this.rig = rig;
        this.service = service;
        this.view = new VBox(8);
        //this.grid = new FlowPane(4,4);
        this.universe = universe;
        buildUI();
    }

    private void buildUI() {

        view.setPadding(new Insets(8, 10, 8, 10));
        //view.getStyleClass().add("hw-fix-header");
        view.setStyle(
                "-fx-background-color: #00040f; " +
                        "-fx-border-color: #oa2a4a;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("STAGE VIEW - UNIVERSE 1 ");
        //title.getStyleClass().add("hw-panel-title");
        title.setStyle(
                "-fx-font-family: 'Orbitron';" +
                        "-fx-font-size: 8px;" +
                        "-fx-text-fill: #2a5a8a;" +
                        "-fx-letter-spacing: 3;"
        );

        // grid.setPadding(new Insets(6));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle liveDot = new Circle(4);
        liveDot.setFill(Color.web("#3aff6a"));
        liveDot.setEffect(new DropShadow(4, Color.web("3aff6a")));

        Label liveLabel = new Label("LIVE");
        liveLabel.setStyle(
                "-fx-font-family: 'Share Tech Mono';" +
                        "-fx-font-size: 9px;" +
                        "-fx-text-fill: #3aff6a;"
        );

        header.getChildren().addAll(title, spacer, liveDot, liveLabel);

        HBox statsRow = new HBox(4);
        statActive = buildStatBox("0", "ACTIVE", "#44aaff");
        statChase = buildStatBox("0", "CHASE", "ffe53a");
        statManual = buildStatBox("0", "MANUAL", "#3aff6a");
        statDark = buildStatBox("0", "DARK", "#1a3a5a");
        statsRow.getChildren().addAll(
                statActive, buildStatDivider(),
                statChase, buildStatDivider(),
                statManual, buildStatDivider(),
                statDark
        );

        // GRID DE FIXTURES
        VBox parSection = buildFixtureSection("PAR LEDS", false);
        VBox headSelection = buildFixtureSection("MOVING HEADS", true);

        HBox legend = new HBox(16);
        legend.setPadding(new Insets(4, 0, 0, 0));
        legend.setStyle(
                "-fx-border-color: #0a1a2a; --fx-border-width: 1 0 0 0;"
        );
        legend.getChildren().addAll(
                buildLegendItem("#3aff6a", "Manual override"),
                buildLegendItem("#ffe53a", "Chase control"),
                buildLegendItem("#1a3a5a", "Dark / off")
        );

        view.getChildren().addAll(
                header, statsRow, parSection, headSelection, legend
        );
    }

    private VBox buildFixtureSection(String sectionTitle,
                                     boolean isHead) {
        VBox section = new VBox(4);
        Label title = new Label(sectionTitle);
        title.setStyle(
                "-fx-font-family: 'Share Tech Mono';" +
                        "-fx-font-size: 7px;" +
                        "-fx-text-fill: #1a3a5a;" +
                        "-fx-padding: 2 0 0 0;"
        );

        FlowPane grid = new FlowPane(5, 5);

        for (Fixture f : rig) {
            boolean isHeadFixture = f.getName().startsWith("HEAD");
            if (isHead != isHeadFixture) continue;

            StackPane cell = new StackPane();

            Shape swatch;
            if (isHead) {
                Circle c = new Circle(18);
                c.setFill(Color.web("#0a1a2a"));
                c.setStroke(Color.web("#0a2a4a"));
                c.setStrokeWidth(1);
                swatch = c;
            } else {
                Rectangle r = new Rectangle(34, 34);
                r.setFill(Color.web("#0a2a4a"));
                r.setStrokeWidth(1);
                r.setArcWidth(4);
                r.setArcHeight(4);
                swatch = r;
            }
            swatches.put(f, swatch);

            Circle sourceDot = new Circle(3);
            sourceDot.setFill(Color.web("#1a3a5a"));
            StackPane.setAlignment(sourceDot, Pos.TOP_RIGHT);
            sourceDots.put(f, sourceDot);

            Label nameLabel = new Label(
                    f.getName().replace("PAR ", "P")
                            .replace("HEAD ", "H")
            );
            nameLabel.setStyle(
                    "-fx-font-family: 'Share Tech Mono';" +
                            "-fx-font-size: 7px;" +
                            "-fx-text-fill: #1a3a5a;"
            );
            StackPane.setAlignment(nameLabel, Pos.BOTTOM_CENTER);
            cell.getChildren().addAll(swatch, sourceDot, nameLabel);

            Tooltip tip = new Tooltip(
                    f.getName() + "@" + f.getAddress() +
                            "\nProfile: " + f.getProfile().getName()
            );
            Tooltip.install(cell, tip);

            grid.getChildren().addAll(cell);
        }
        section.getChildren().addAll(title, grid);
        return section;
    }


    // refresh se llamara en cada 'refresh' del sistema.
    public void refresh() {

        int activeCount = 0;
        int chaseCount = 0;
        int manualCount = 0;
        int darkCount = 0;

        if(universe.isGlobalBlackout()){
            for (Fixture f : rig){
                Shape swatch = swatches.get(f);
                Circle sourceDot = sourceDots.get(f);
                if(swatch == null) continue;
                swatch.setFill(Color.web("#0a1a2a"));
                swatch.setStroke(Color.web("#ff4444"));
                swatch.setEffect(null);
                if(sourceDot != null) sourceDot.setFill(Color.web("#ff4444"));
            }
            statActive.setText("0");
            statChase.setText("0");
            statManual.setText("0");
            statDark.setText(String.valueOf(rig.size()));
            return;  //no procesa nada
        }

        for (Fixture f : rig) {
            int strobe = service.getChannelValue(f, ChannelFunction.STROBE);
            Shape swatch = swatches.get(f);
            Circle sourceDot = sourceDots.get(f);
            if (swatch == null || sourceDot == null) continue;

            int r = service.getChannelValue(f, ChannelFunction.RED);
            int g = service.getChannelValue(f, ChannelFunction.GREEN);
            int b = service.getChannelValue(f, ChannelFunction.BLUE);
            int y = service.getChannelValue(f, ChannelFunction.YELLOW);
            int w = service.getChannelValue(f, ChannelFunction.WHITE);
            int dimmer = service.getChannelValue(f, ChannelFunction.DIMMER);
            double factor = dimmer / 255.0;

            int previewR = (int) (Math.min(255, r + y) * factor);
            int previewG = (int) (Math.min(255, g + (y / 2) + w) * factor);
            int previewB = (int) (Math.min(255, b + w) * factor);

            boolean isDark = (previewR + previewG + previewB) < 8;

            int baseAddr = f.getAddress();
            Universe.Source source = universe.getSource(baseAddr + 1);


            Color color = isDark
                    ? Color.web("#0a1a2a")
                    : Color.rgb(previewR, previewG, previewB);
            swatch.setFill(color);

            if (isDark) {
                swatch.setStroke(Color.web("#0a1a2a"));
                swatch.setEffect(null);
                sourceDot.setFill(Color.web("#1a3a5a"));
                darkCount++;

            } else if (source == Universe.Source.CHASE) {
                swatch.setStroke(Color.web("#ffe53a"));
                swatch.setEffect(
                        new DropShadow(6, Color.rgb(previewR, previewG, previewB)));
                sourceDot.setFill(Color.web("#ffe53a"));
                chaseCount++;
                activeCount++;
            } else {
                swatch.setStroke(Color.web("#3aff6a"));
                swatch.setEffect(
                        new DropShadow(6, Color.rgb(previewR, previewG, previewB)));
                sourceDot.setFill(Color.web("#3aff6a"));
                manualCount++;
                activeCount++;
            }

            if (strobe > 0 && !isDark) {
                swatch.setStroke(Color.web("#aa3aff"));
                swatch.setStrokeWidth(2);
            } else {
                swatch.setStrokeWidth(1);
            }

            final int ac = activeCount, cc = chaseCount,
                    mc = manualCount, dc = darkCount;

            statActive.setText(String.valueOf(ac));
            statChase.setText(String.valueOf(cc));
            statManual.setText(String.valueOf(mc));
            statDark.setText(String.valueOf(dc));

        }
    }

    private Label buildStatBox (String val, String lbl, String color){
        VBox box = new VBox(1);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(2, 8, 2, 8));
        box.setStyle(
                "-fx-background-color: #00020a;" +
                        "-fx-border-color: #0a1a2a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 2;" +
                        "-fx-background-radius: 2;"
        );

        Label valLabel = new Label(val);
        valLabel.setStyle(
                "-fx-font-family: 'VT323';" +
                        "-fx-font-size: 16px;" +
                        "-fx-text-fill: " + color + ";"
        );

        Label lblLabel = new Label(lbl);
        lblLabel.setStyle(
                "-fx-font-family: 'Share Tech Mono';" +
                        "-fx-font-size: 6px;" +
                        "-fx-text-fill: #1a3a5a;"
        );

        box.getChildren().addAll(valLabel, lblLabel);
        HBox.setHgrow(box, Priority.ALWAYS);

        return valLabel;
    }

    private Region buildStatDivider () {
        Region div = new Region();
        div.setPrefWidth(1);
        div.setMaxWidth(1);
        div.setStyle("-fx-background-color: #0a1a2a;");
        return div;
    }

    private HBox buildLegendItem (String color, String text){
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4);
        dot.setFill(Color.web(color));
        Label lbl = new Label(text);
        lbl.setStyle(
                "-fx-font-family: 'Share Tech Mono';" +
                        "-fx-font-size: 8px;" +
                        "-fx-text-fill: #2a5a8a;"
        );
        item.getChildren().addAll(dot, lbl);
        return item;
    }

    public VBox getView() {
        return view;
    }
/// Metodo para mostrar en negro todos los fixtures al presionar STOP
    public void restart(){
        int activeCount = 0;
        int chaseCount = 0;
        int manualCount = 0;
        int darkCount = 0;
        for (Fixture f : rig){
            Shape swatch = swatches.get(f);
            Circle sourceDot = sourceDots.get(f);
            if(swatch == null) continue;
            swatch.setFill(Color.web("#0a1a2a"));
            swatch.setStroke(Color.web("#ff4444"));
            swatch.setEffect(null);
            if(sourceDot != null) sourceDot.setFill(Color.web("#ff4444"));
        }
        statActive.setText("0");
        statChase.setText("0");
        statManual.setText("0");
        statDark.setText(String.valueOf(rig.size()));
        return;  //no procesa nada
    }


}
