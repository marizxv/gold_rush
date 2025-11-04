package edu.io.player;

import edu.io.token.*;

public class Player {
    private PlayerToken token;
    public final Gold gold = new Gold();
    private Shed shed = new Shed();

    public void assignToken(PlayerToken token) {
        this.token = token;
    }

    public PlayerToken token() {
        return token;
    }

    public Shed shed() {
        return shed;
    }

    public void interactWithToken(Token token) {
        switch (token) {
            case GoldToken goldToken -> useToolOnGold(goldToken);
            case PickaxeToken pickaxeToken -> shed.add(pickaxeToken);
            case AnvilToken anvilToken -> {
                Tool tool = shed.getTool();
                if (tool instanceof Repairable repairableTool) {
                    repairableTool.repair();
                }
            }
            default -> { }
        }
    }

    private void useToolOnGold(GoldToken goldToken) {
        Tool tool = shed.getTool();
        double amount = goldToken.amount();

        tool.useWith(goldToken)
                .ifWorking(() -> gold.gain(amount * getGainFactor(tool)))
                .ifBroken(() -> {
                    gold.gain(amount);
                    shed.dropTool();
                })
                .ifIdle(() -> gold.gain(amount));
    }

    private double getGainFactor(Tool tool) {
        if (tool instanceof PickaxeToken pickaxe) {
            return pickaxe.gainFactor();
        }
        return 1.0;
    }
}