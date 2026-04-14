package com.example.milkmate.domain.model;

/**
 * Represents a chat bubble in the delivery screen.
 * TYPE_SYSTEM = gray center bubble, TYPE_MILKMAN = blue right-aligned.
 */
public class ChatBubbleItem {

    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_MILKMAN = 1;

    public final int type;
    public final String label;  // e.g. "Morning", "Evening"
    public final String message;

    public ChatBubbleItem(int type, String label, String message) {
        this.type = type;
        this.label = label != null ? label : "";
        this.message = message != null ? message : "";
    }

    public static ChatBubbleItem system(String message) {
        return new ChatBubbleItem(TYPE_SYSTEM, "", message);
    }

    public static ChatBubbleItem milkman(String label, String message) {
        return new ChatBubbleItem(TYPE_MILKMAN, label, message);
    }
}
