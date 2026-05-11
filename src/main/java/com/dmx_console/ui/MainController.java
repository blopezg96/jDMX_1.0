package com.dmx_console.ui;

import com.dmx_console.model.Fixture;
import javafx.scene.layout.BorderPane;
import com.dmx_console.service.FixtureService;

import java.util.List;

public class MainController {

    private final List<Fixture> rig;
    private final FixtureService service;
    private final BorderPane view;

    public MainController(List<Fixture> rig, FixtureService service){

        this.rig = rig;
        this.service = service;
        this.view = new BorderPane();
        buildUI();
    }

    private void buildUI(){
        // aqui construimos la UI paso a paso
    }

    public BorderPane getView(){
        return view;
    }
}
