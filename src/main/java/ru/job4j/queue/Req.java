package ru.job4j.queue;

import java.util.List;

/**
 * Парсинг входящего запроса.
 * httpRequestType - GET или POST, указывает на тип запроса.
 * poohMode - указывает на режим работы: queue или topic
 * sourceName - имя очереди или топика
 * param - содержимое запроса
 */
public class Req {
    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        /* TODO parse a content */
        String type = "";
        String mode = "";
        String name = "";
        String parameter = "";

        List<String> list = content.lines().toList();
        String first = list.get(0);
        String last = list.get(list.size() - 1);

        String[] middleFirst = first.split(" ", 3);
        String[] middle = middleFirst[1].split("/");

        if (first.startsWith("POST")) {
            type = "POST";
            parameter = last;
        } else if (first.startsWith("GET")) {
            type = "GET";
            if (middle[1].equals("topic")) {
                parameter = middle[3];
            }
        }
        mode = middle[1];
        name = middle[2];
        return new Req(type, mode, name, parameter);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}
