package edu.io.player;

import edu.io.player.VitalsValues;
import edu.io.token.*;
import java.util.Objects;

public class Player {
    private PlayerToken token;
    public final Gold gold = new Gold();
    private Shed shed = new Shed();
    public final Vitals vitals = new Vitals();

    public void assignToken(PlayerToken token) {
        this.token = Objects.requireNonNull(token, "Token cannot be null");
    }

    public PlayerToken token() {
        return token;
    }

    public Shed shed() {
        return shed;
    }

    public void interactWithToken(Token token) {
        Objects.requireNonNull(token, "Token cannot be null");

        if (!vitals.isAlive()) {
            throw new IllegalStateException("Player is dead");
        }

        switch (token) {
            case GoldToken goldToken -> {
                usePickaxeOnGold(goldToken);
                vitals.dehydrate(VitalsValues.DEHYDRATION_GOLD);
            }
            case PickaxeToken pickaxeToken -> {
                shed.add(pickaxeToken);
                // Nie zużywamy wody przy podnoszeniu kilofa - zgodnie z testami
            }
            case AnvilToken anvilToken -> {
                Tool tool = shed.getTool();
                if (tool instanceof Repairable repairableTool) {
                    repairableTool.repair();
                }
                vitals.dehydrate(VitalsValues.DEHYDRATION_ANVIL);
            }
            case WaterToken waterToken -> {
                vitals.hydrate(waterToken.amount());
            }
            default -> {
                // Zużycie wody dla ruchu (EmptyToken i inne nieznane tokeny)
                vitals.dehydrate(VitalsValues.DEHYDRATION_MOVE);
            }
        }
    }

    private void usePickaxeOnGold(GoldToken goldToken) {
        Tool tool = shed.getTool();
        double amount = goldToken.amount();

        if (tool instanceof PickaxeToken pickaxe) {
            pickaxe.useWith(goldToken)
                    .ifWorking(() -> gold.gain(amount * pickaxe.gainFactor()))
                    .ifBroken(() -> {
                        gold.gain(amount);
                        shed.dropTool();
                    })
                    .ifIdle(() -> gold.gain(amount));
        } else {
            tool.useWith(goldToken)
                    .ifWorking(() -> gold.gain(amount * getGainFactor(tool)))
                    .ifBroken(() -> {
                        gold.gain(amount);
                        shed.dropTool();
                    })
                    .ifIdle(() -> gold.gain(amount));
        }
    }

    private double getGainFactor(Tool tool) {
        if (tool instanceof PickaxeToken pickaxe) {
            return pickaxe.gainFactor();
        }
        return 1.0;
    }
}