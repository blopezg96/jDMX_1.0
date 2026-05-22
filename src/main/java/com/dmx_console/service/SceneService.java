package com.dmx_console.service;

import com.dmx_console.model.Fixture;
import com.dmx_console.model.Scene;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SceneService {
    private final FixtureService fixtureService;
    private final List<Scene> scenes;
    private final Gson gson;
    private static final String SCENES_FILES = "scenes.json";

    public SceneService(FixtureService fixtureService){
        this.fixtureService = fixtureService;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.scenes = loadFromDisk();
    }

    // Generamos una escena a partir de los valores almacenados de cada fixture.
    public Scene capture(String name, List<Fixture> rig){
        Scene scene = new Scene(name);
        for(Fixture fixture : rig){
            for(var channel : fixture.getProfile().getChannels()){
                int value = fixtureService.getChannelValue(
                        fixture, channel.getFunction()
                );
                scene.setValues(
                        fixture.getName(),
                        channel.getFunction(),
                        value
                );
            }
        }
        scenes.add(scene);
        saveToDisk();
        return scene;

    }

    // Se asigna una escena a todos los valores almacenados en el rig
    public void apply(Scene scene, List<Fixture> rig){
        for(Fixture fixture :  rig){
            for(var channel : fixture.getProfile().getChannels()){
                int value = scene.getValue(
                        fixture.getName(),
                        channel.getFunction()
                );
                fixtureService.setChannel(
                        fixture,
                        channel.getFunction(),
                        value
                );
            }
        }
    }




    // Metodo para eliminar escenas por su nombre
    public void delete(String name){
        scenes.removeIf(s -> s.getName().equals(name));
        saveToDisk();
    }

    public List<Scene> getScenes(){
        return scenes;
    }

    // Metodos de persistencia

    private void saveToDisk(){
        try(Writer writer = new FileWriter(SCENES_FILES)){
            gson.toJson(scenes, writer);
            System.out.println("[SCENES] Guardadas " + scenes.size()
            + " escenas.");
        } catch (IOException e) {
            System.err.println("[SCENES] Error al guardar: "+
                    e.getMessage());
        }
    }

    private List<Scene> loadFromDisk(){
        File file = new File(SCENES_FILES);
        if(!file.exists()){
            System.out.println("[SCENES] No hay escenas guardadas aun. ");
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)){
            Type listType = new TypeToken<List<Scene>>()
            {}.getType();
            List<Scene> loaded = gson.fromJson(reader, listType);
            System.out.println("[SCENES] cargadas " + loaded.size()
            + "escenas ");
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e){
            System.err.println("[SCENES] Error al cargar: "+
                    e.getMessage());
            return  new ArrayList<>();
        }
    }

}
