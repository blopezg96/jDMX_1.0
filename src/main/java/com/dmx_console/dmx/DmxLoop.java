package com.dmx_console.dmx;

import com.dmx_console.output.DMXOutput;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DmxLoop {
    private static final int DMX_FPS = 44;
    private static final long INTERVAL_MS = 1000 / DMX_FPS;

    private final Universe universe;
    private final DMXOutput output;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> loopTask;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private long frameCount = 0;
    private long lastStatsTime = System.currentTimeMillis();

    public DmxLoop(Universe universe, DMXOutput output){
        this.universe = universe;
        this.output = output;

        this.executor = Executors.newSingleThreadScheduledExecutor(r ->{
            Thread t = new Thread(r, "DMX-Loop");
            t.setDaemon(true);
            return t;
        });
    }

    public void start(){
        if(running.get()) return;
        output.connect();
        running.set(true);

        loopTask = executor.scheduleAtFixedRate(() -> {
            try {
                byte[] data = universe.getSnapShot();
                output.sendUniverse(data);
                frameCount++;
                logStats();
            } catch (Exception e){
                System.err.println("[DMX-LOOP] Error: " +
                        e.getMessage());
            }
        }, 0, INTERVAL_MS, TimeUnit.MILLISECONDS);

        System.out.println("[DMX-LOOP] Iniciado a " + DMX_FPS + "" +
                "fps");
     }
     public void stop(){
        if(!running.get()) return;
        running.set(false);

        if(loopTask != null){
            loopTask.cancel(false);
        }
        executor.shutdown();
        output.disconnect();
        System.out.println("[DMX-LOOP] Detenido. ");
     }

     public boolean isRunning(){
        return running.get();
     }

     private void logStats(){

        long now = System.currentTimeMillis();
        if(now - lastStatsTime >= 5000){
            double elapsed = (now - lastStatsTime) / 1000.0;
            double fps = frameCount / elapsed;
            System.out.printf("[JDMX-LOOP] FPS reales: %.1f%n", fps);
            frameCount = 0;
            lastStatsTime = now;
        }
    }
}
