package edu.io.token;

public class PickaxeToken extends Token implements Tool, Repairable {
    private final double gainFactor;
    private int durability;
    private final int initialDurability;

    public PickaxeToken() {
        this(1.5, 10);
    }

    public PickaxeToken(double gainFactor) {
        this(gainFactor, 10);
    }

    public PickaxeToken(double gainFactor, int durability) {
        super(Label.PICKAXE_TOKEN_LABEL);
        if (gainFactor <= 0) {
            throw new IllegalArgumentException("Gain factor must be positive");
        }
        if (durability <= 0) {
            throw new IllegalArgumentException("Durability must be positive");
        }
        this.gainFactor = gainFactor;
        this.durability = durability;
        this.initialDurability = durability;
    }

    public double gainFactor() {
        return gainFactor;
    }

    public int durability() {
        return durability;
    }

    public void use() {
        if (durability > 0) {
            durability--;
        }
    }

    @Override
    public void repair() {
        durability = initialDurability;
    }

    @Override
    public boolean isBroken() {
        return durability <= 0;
    }

    @Override
    public Tool useWith(Token withToken) {
        if (withToken instanceof GoldToken && !isBroken()) {
            use();
        }
        return this;
    }

    @Override
    public Tool ifWorking(Runnable action) {
        if (!isBroken()) {
            action.run();
        }
        return this;
    }

    @Override
    public Tool ifBroken(Runnable action) {
        if (isBroken()) {
            action.run();
        }
        return this;
    }

    @Override
    public Tool ifIdle(Runnable action) {
        return this;
    }
}