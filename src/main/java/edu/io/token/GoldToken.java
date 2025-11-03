package edu.io.token;

public class GoldToken extends Token {
    private final double amount;

    public GoldToken() {
        this(1.0);
    }

    public GoldToken(double amount) {
        super(Label.GOLD_TOKEN_LABEL);
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    public double amount() {
        return amount;
    }
}