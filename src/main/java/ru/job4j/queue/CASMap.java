package ru.job4j.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class CASMap {
    public static void main(String[] args) {
        ConcurrentMap queue = new ConcurrentHashMap<>();
        String name = "weather";

        /* add if empty */
        queue.putIfAbsent(name, new ConcurrentLinkedQueue<>());

        /* put */
        queue.get(name).add("value");

        /* extract */
        var text = queue.get(name, emptyQueue()).poll();
    }
}
