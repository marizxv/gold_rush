package edu.io.player;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Vitals {
    private int hydration;
    private Runnable onDeathCallback;

    public Vitals(){
        this.hydration = 100;
        this.onDeathCallback = () -> {};
    }

    public int hydration() {
        return hydration;
    }

    public void hydrate(int amount){
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
        hydration = Math.min(hydration + amount, 100);
    }

    public void dehydrate(int amount){
        if (amount < 0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        int newHydration = Math.max(hydration - amount, 0);
        hydration = newHydration;

        if (newHydration == 0)
            onDeathCallback.run();
    }

    public boolean isAlive(){
        return hydration > 0;
    }

    public void setOnDeathHandler(@NotNull Runnable callback) {
        this.onDeathCallback = Objects.requireNonNull(callback, "callback cannot be null");
    }
}
