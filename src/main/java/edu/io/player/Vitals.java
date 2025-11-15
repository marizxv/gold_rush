package edu.io.player;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Vitals {
    private int hydration;
    private Runnable onDeathCallback;

    public Vitals(){
        this.hydration = 100;
        this.onDeathCallback = () -> {};
    };

    public int hydration() {
        return hydration;
    };

    public void hydrate(int amount){
        if (hydration < 0)
            throw new IllegalArgumentException("Hydration must be a positive number");
        hydration = Math.min(hydration + amount, 100);
    };

    public void dehydrate(int amount){
        if (hydration < 0)
            throw new IllegalArgumentException("Hydration must be a positive number");
        hydration = Math.max(amount - hydration, 0);

        if (hydration <= 0)
            onDeathCallback.run();
    };

    private boolean isAlive(){
        return hydration > 0;
    };

    public void setOnDeathHandler(@NotNull Runnable callback) {
        this.onDeathCallback = Objects.requireNonNull(callback, "callback cannot be null");
    }
}
