package ru.otus.model;

import java.util.List;

public class ObjectForMessage {
    private List<String> data;

    public ObjectForMessage() {
        super();
    }

    public ObjectForMessage(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String toString() {
        return "ObjectForMessage" + data.toString();
    }
}
