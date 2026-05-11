package com.dmx_console.main;

import com.dmx_console.model.Fixture;
import com.dmx_console.dmx.Universe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.dmx_console.output.DMXOutput;
import com.dmx_console.output.SimulatedDMXOutput;
import com.dmx_console.service.FixtureService;
import com.dmx_console.setup.ShowSetup;
import com.dmx_console.ui.MainController;

import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage stage){

        // --- BACKEND ---
        Universe universe = new Universe();
        DMXOutput output = new SimulatedDMXOutput();
        FixtureService service = new FixtureService(universe, output);

        List<Fixture> rig = ShowSetup.buildRig();
        output.connect();

        // --- UI ----
        MainController controller = new MainController(rig, service);

        Scene scene = new Scene(controller.getView(), 900, 600);
        stage.setTitle("jDMX Console");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
