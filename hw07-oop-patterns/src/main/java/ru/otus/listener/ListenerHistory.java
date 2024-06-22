package ru.otus.listener;

import java.util.*;

import ru.otus.model.Message;

public class ListenerHistory implements Listener, HistoryReader {
    private final HashMap<Long, Message> history = new HashMap<>();

    public HashMap<Long, Message> getHistory() {
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
