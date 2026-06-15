package com.dmx_console.service;

import com.dmx_console.model.Chase;
import com.dmx_console.model.Fixture;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.time.Clock.tick;
import static jdk.jfr.internal.consumer.EventLog.stop;

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

    // Callback que notifica a la UI que chase esta activo
    private Consumer<Integer> onStepChanged;

    public ChaseEngine(SceneService sceneService, List<Fixture> rig){
        this.sceneService = sceneService;
        this.rig = rig;
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

}
