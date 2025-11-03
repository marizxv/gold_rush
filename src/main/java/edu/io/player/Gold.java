package edu.io.player;

public class Gold {
    private double amount = 0.0;

    public Gold() {}

    public Gold(double amount) {
        this.amount = amount;
    }

    public double amount() {
        return amount;
    }

    public void gain(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount += amount;
    }

    public void lose(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount > this.amount) {
            throw new IllegalArgumentException("Not enough gold");
        }
        this.amount -= amount;
    }
}