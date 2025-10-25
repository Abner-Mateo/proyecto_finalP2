package com.example.prox0.model;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private String timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = java.text.DateFormat.getTimeInstance().format(new java.util.Date());
    }

    // Getters y Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isUser() { return isUser; }
    public void setUser(boolean user) { isUser = user; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
