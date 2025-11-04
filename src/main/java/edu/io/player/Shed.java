package edu.io.player;

import edu.io.token.Tool;
import java.util.Stack;

public class Shed {
    private Stack<Tool> tools = new Stack<>();

    public boolean isEmpty() {
        return tools.isEmpty();
    }

    public void add(Tool tool) {
        if (tool == null) {
            throw new IllegalArgumentException("Tool cannot be null");
        }
        tools.push(tool);
    }

    public Tool getTool() {
        if (tools.isEmpty()) {
            return NoTool.getInstance();
        }
        return tools.peek();
    }

    public void dropTool() {
        if (!tools.isEmpty()) {
            tools.pop();
        }
    }
}