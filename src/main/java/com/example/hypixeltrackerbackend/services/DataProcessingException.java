package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.data.bazaar.CompleteItem;
import com.example.hypixeltrackerbackend.data.bazaar.Craft;

import java.io.IOException;

public class DataProcessingException extends IOException {
    private final transient CompleteItem completeItem;
    private final transient Craft craft;

    public DataProcessingException(String message) {
        super(message);
        this.completeItem = null;
        this.craft = null;
    }

    public DataProcessingException(String message, Craft craft) {
        super(message);
        this.craft = craft;
        this.completeItem = null;
    }

    public DataProcessingException(String message, CompleteItem completeItem) {
        super(message);
        this.completeItem = completeItem;
        this.craft = null;
    }

    public DataProcessingException(String message, CompleteItem completeItem, Craft craft) {
        super(message);
        this.completeItem = completeItem;
        this.craft = craft;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        if (completeItem != null) {
            builder.append("[item:").append(completeItem.getName()).append("] ");
        }
        if (craft != null) {
            builder.append("[craft:").append(craft).append("] ");
        }
        builder.append(super.getMessage());
        return builder.toString();
    }
}
