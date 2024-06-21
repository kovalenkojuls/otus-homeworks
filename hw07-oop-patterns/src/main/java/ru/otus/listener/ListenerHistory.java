package ru.otus.listener;

import java.util.*;

import ru.otus.model.Message;

public class ListenerHistory implements Listener, HistoryReader {
    private final TreeMap<Long, Message> history = new TreeMap<>();

    public TreeMap<Long, Message> getHistory() {
        return history;
    }

    @Override
    public void onUpdated(Message msg) {
        var newMsg = msg.toBuilder().build();
        history.put(newMsg.getId(), newMsg);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(history.get(id));
    }
}
