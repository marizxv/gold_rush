package edu.io.player;

import edu.io.token.*;

public class Player {
    private PlayerToken token;
    public final Gold gold = new Gold();
    public final Shed shed = new Shed();

    public void assignToken(PlayerToken token) {
        this.token = token;
    }

    public PlayerToken token() {
        return token;
    }

    public double gold() {
        return gold.amount();
    }

    public void gainGold(double amount) {
        gold.gain(amount);
    }

    public void loseGold(double amount) {
        gold.lose(amount);
    }

    public void interactWithToken(Token token) {
        switch (token) {
            case GoldToken goldToken -> {
                final double baseAmount = goldToken.amount(); // final variable
                Tool tool = shed.getTool();

                if (tool instanceof PickaxeToken pickaxe) {
                    // używamy finalnej zmiennej wewnątrz lambd
                    tool.useWith(goldToken)
                            .ifWorking(() -> {
                                double enhancedAmount = baseAmount * pickaxe.gainFactor();
                                pickaxe.use();
                                if (pickaxe.isBroken()) {
                                    shed.dropTool();
                                }
                                gainGold(enhancedAmount);
                            })
                            .ifIdle(() -> {
                                gainGold(baseAmount);
                            });
                } else {
                    // gdy nie ma kilofu, po prostu zbierz złoto
                    gainGold(baseAmount);
                }
            }
            case PickaxeToken pickaxeToken -> {
                shed.add(pickaxeToken);
            }
            case AnvilToken anvilToken -> {
                Tool tool = shed.getTool();
                if (tool instanceof Repairable repairableTool) {
                    repairableTool.repair();
                }
            }
            default -> {
                // ignore other tokens
            }
        }
    }
}