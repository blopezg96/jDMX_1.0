package com.dmx_console.service;

import com.dmx_console.model.Chase;
import com.dmx_console.model.Fixture;
import com.dmx_console.model.Scene;
import javafx.application.Platform;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.time.Clock.tick;


public class ChaseEngine {

    private final SceneService sceneService;
    private final List<Fixture> rig;

    private Chase currentChase;
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> chaseTask;

    private int currentStep = 0;
    private int bounceDirection = 1;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Random random = new Random();
    private Runnable onTick;

    // Callback que notifica a la UI que chase esta activo
    private Consumer<Integer> onStepChanged;

    public ChaseEngine(SceneService sceneService, List<Fixture> rig){
        this.sceneService = sceneService;
        this.rig = rig;
    }

    public void setOnTick(Runnable callback){
        this.onTick = callback;
    }

    public void setOnStepChanged(Consumer<Integer> callback){
        this.onStepChanged = callback;
    }

    // Iniciar un chase
    public void play(Chase chase){
        stop();

        if(chase.getSteps().isEmpty()){
            System.out.println("[CHASE] No hay pasos en el chase. ");
            return;
        }

        this.currentChase = chase;
        this.currentStep = 0;
        this.bounceDirection = 1;
        this.running.set(true);

        executor = Executors.newSingleThreadScheduledExecutor(r ->{
            Thread t = new Thread(r, "Chase-Engine");
            t.setDaemon(true);
            return t;
        });

        long intervalMs = chase.getIntervalMs();

        chaseTask = executor.scheduleAtFixedRate(() -> {
            try {
                tick();
            } catch (Exception e){
                System.err.println("[CHASE] Error: " + e.getMessage());
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);

        System.out.printf("[CHASE] '%s' iniciando a %1f BPM (%dms/paso) modo %s%n",
                chase.getName(), chase.getBpm(),
                intervalMs, chase.getMode());
    }

    // Pausar
    public void pause(){
        if(chaseTask != null){
           chaseTask.cancel(false);
           chaseTask = null;
        }
        running.set(false);
        System.out.println("[CHASE] Pausado en paso " +currentStep);
    }

    // Reanudar
    public void resume(){
        if(currentChase == null || running.get()) return;
        running.set(true);

        long intervalMs = currentChase.getIntervalMs();
        chaseTask = executor.scheduleAtFixedRate(() -> {
            try{
                tick();
            } catch(Exception e){
                System.err.println("[CHASE] Error " + e.getMessage());
            }
        }, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
        System.out.println("[CHASE] Reanudado. ");
    }

    //Detener

    public void stop(){
        running.set(false);
        if(chaseTask != null){
            chaseTask.cancel(false);
            chaseTask = null;
        }
        if(executor != null){
            executor.shutdown();
            executor = null;
        }
        currentStep = 0;
        bounceDirection = 1;
        System.out.println("[CHASE] Detenido");
    }

    // cambiar velocidad
    public void setBpm(double bpm){
        if(currentChase == null) return;
        currentChase.setBpm(bpm);
        if(running.get()){
            play(currentChase);
        }
    }

    public boolean isRunning(){return running.get(); }
    public Chase getCurrentChase(){
        return currentChase;
    }
    public int getCurrentStep(){
        return currentStep;
    }

    // LOGICA DE AVANCE DE PASOS

    private void tick(){
        List<Scene> steps = currentChase.getSteps();
        if(steps.isEmpty()) return;

        Scene scene = steps.get(currentStep);
        sceneService.apply(scene, rig);

        System.out.printf("[CHASE] Paso %d%d - '%s'%n",
        currentStep + 1, steps.size(), scene.getName());

        // notificar a la UI
        if(onStepChanged != null){
            final int step = currentStep;
            Platform.runLater(() ->
                    onStepChanged.accept(step));

        }

        if(onTick != null){
            Platform.runLater(onTick);
        }

        // Siguiente paso
        switch(currentChase.getMode()) {
            case FORWARD -> nextForward(steps.size());
            case BACKWARD -> nextBackward(steps.size());
            case BOUNCE -> nextBounce(steps.size());
            case RANDOM -> nextRandom(steps.size());
        }
    }

    private void nextForward(int size){
        currentStep = (currentStep +1) % size;
    }
    private void nextBackward(int size){
        currentStep = (currentStep + 1) % size;
    }
    private void nextBounce(int size){
        if(size <=1) return;
        currentStep += bounceDirection;
        if(currentStep>= size -1){
            currentStep = size - 1;
            bounceDirection = size - 1;
        } else if (currentStep<=0) {
            currentStep = 0;
            bounceDirection = 1;

        }
    }

    private void nextRandom(int size){
        int next;
        do{
            next = random.nextInt(size);
        } while (next == currentStep && size > 1);
        currentStep = next;
    }

}
