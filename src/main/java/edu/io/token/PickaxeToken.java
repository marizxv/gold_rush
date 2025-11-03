package edu.io.token;

public class PickaxeToken extends Token implements Tool, Repairable {
    private final double gainFactor;
    private int durability;
    private final int maxDurability;

    public PickaxeToken() {
        this(1.5, 5);
    }

    public PickaxeToken(double gainFactor, int durability) {
        super(Label.PICKAXE_TOKEN_LABEL);
        this.gainFactor = gainFactor;
        this.durability = durability;
        this.maxDurability = durability;
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
        durability = maxDurability;
    }

    @Override
    public boolean isBroken() {
        return durability <= 0;
    }

    @Override
    public Tool useWith(Token withToken) {
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
        action.run();
        return this;
    }
}