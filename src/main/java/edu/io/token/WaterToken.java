package edu.io.token;

public class WaterToken extends Token {
    private final int amount;

    public WaterToken() {
        this(10); // Domyślna ilość *zgodnie z testami!!!!*
    }

    public WaterToken(int amount) {
        super(Label.WATER_TOKEN_LABEL);
        if (amount <= 0 || amount > 100) {
            throw new IllegalArgumentException("Amount must be between 1 and 100");
        }
        this.amount = amount;
    }

    public int amount() {
        return amount;
    }
}