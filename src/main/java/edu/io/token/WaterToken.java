package edu.io.token;

public class WaterToken extends Token {
    private final int amount;

    public WaterToken() {
        this(25);
    }

    public WaterToken(int amount) {
        super(Label.WATER_TOKEN_LABEL);
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = amount;
    }

    public int amount() {
        return amount;
    }
}