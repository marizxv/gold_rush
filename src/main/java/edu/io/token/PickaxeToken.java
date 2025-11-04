package edu.io.token;

public class PickaxeToken extends Token implements Tool, Repairable {
    private final double gainFactor;
    private int durability;
    private final int initialDurability;
    private boolean wasUsedInLastOperation = false;

    public PickaxeToken() {
        this(1.5, 3);
    }

    public PickaxeToken(double gainFactor) {
        this(gainFactor, 3);
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
        wasUsedInLastOperation = false;

        if (withToken instanceof GoldToken && !isBroken()) {
            use();
            wasUsedInLastOperation = true;
        }
        return this;
    }

    @Override
    public Tool ifWorking(Runnable action) {
        if (wasUsedInLastOperation && !isBroken()) {
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
        if (!wasUsedInLastOperation && !isBroken()) {
            action.run();
        }
        return this;
    }
}