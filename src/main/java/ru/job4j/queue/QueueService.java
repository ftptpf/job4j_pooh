package ru.job4j.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Отправитель посылает запрос на добавление данных с указанием очереди (weather) и значением параметра (temperature=18).
 * Сообщение помещается в конец очереди. Если очереди нет в сервисе, то нужно создать новую и поместить в нее сообщение.
 *
 * Получатель посылает запрос на получение данных с указанием очереди. Сообщение забирается из начала очереди и удаляется.
 * Если в очередь приходят несколько получателей, то они поочередно получают сообщения из очереди.
 * Каждое сообщение в очереди может быть получено только одним получателем.
 */
public class QueueService implements Service {
    private final ConcurrentMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text = "";
        String status = "204";
        String type = req.httpRequestType();
        String key = req.getSourceName();
        String param = req.getParam();
        if ("POST".equals(type)) {
            /* Если нет значения для соответствующего ключа - создаем новое значение (очередь) */
            queue.putIfAbsent(key, new ConcurrentLinkedQueue<>());
            /* Добавляем в очередь полученный параметр */
            queue.get(key).add(param);
        } else if ("GET".equals(type)) {
            /* Извлекаем сообщение из начала очереди при этом удаляя его */
            text = queue.get(key).poll();
            status = "200";
        }
        return new Resp(text, status);
    }
}
