package com.dmx_console.ui;

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.service.FixtureService;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixturePreviewGrid {
    private final List<Fixture> rig;
    private final FixtureService service;
    private final VBox view;
    private final FlowPane grid;
    private final Map<Fixture, Rectangle> swatches = new HashMap<>();

    public FixturePreviewGrid(List<Fixture> rig, FixtureService service){
        this.rig = rig;
        this.service = service;
        this.view = new VBox(6);
        this.grid =new FlowPane(4,4);
        buildUI();
    }

    private void buildUI(){

        view.setPadding(new Insets(8));
        view.getStyleClass().add("hw-fix-header");

        Label title = new Label("LIVE RIG PREVIEW");
        title.getStyleClass().add("hw-panel-title");

        grid.setPadding(new Insets(6));

        for(Fixture f : rig){

            Rectangle swatch = new Rectangle(28, 28);
            swatch.setFill(Color.web("#0a1a2a"));
            swatch.setArcWidth(4);
            swatch.setArcHeight(4);
            swatch.setStroke(Color.web("#1a3a5a"));
            swatch.setStrokeWidth(1);

            Tooltip  tooltip = new Tooltip(f.getName() + "@" + f.getAddress());
            Tooltip.install(swatch, tooltip);

            swatches.put(f, swatch);
            grid.getChildren().add(swatch);
        }

        view.getChildren().addAll(title, grid);

    }

    // refresh se llamara en cada 'refresh' del sistema.
    public void refresh(){
        for(Fixture f : rig){
            Rectangle swatch = swatches.get(f);
            if(swatch == null ) continue;

            int r = service.getChannelValue(f, ChannelFunction.RED);
            int g = service.getChannelValue(f, ChannelFunction.GREEN);
            int b = service.getChannelValue(f, ChannelFunction.BLUE);
            int y = service.getChannelValue(f, ChannelFunction.YELLOW);
            int w = service.getChannelValue(f, ChannelFunction.WHITE);
            int dimmer = service.getChannelValue(f, ChannelFunction.DIMMER);
            double factor = dimmer/255.0;

            int previewR =  (int)(Math.min(255, r+y) * factor);
            int previewG =  (int)(Math.min(255, g + (y/2) + w)* factor);
            int previewB =  (int)(Math.min(255, b +w) * factor);

            Color color = Color.rgb(previewR, previewG, previewB);
            swatch.setFill(color);

            if(previewR + previewG + previewB > 30){
                swatch.setEffect(new DropShadow(6, color));
            } else {
                swatch.setEffect(null);
            }
        }
    }
    public VBox getView(){
        return view;
    }
}
