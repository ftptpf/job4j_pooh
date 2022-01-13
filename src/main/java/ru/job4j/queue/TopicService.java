package ru.job4j.queue;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Отправитель посылает запрос на добавление данных с указанием топика (weather) и значением параметра (temperature=18).
 * Сообщение помещается в конец каждой индивидуальной очереди получателей.
 * Если топика нет в сервисе, то данные игнорируются.
 *
 * Получатель посылает запрос на получение данных с указанием топика. Если топик отсутствует, то создается новый.
 * А если топик присутствует, то сообщение забирается из начала индивидуальной очереди получателя и удаляется.
 * Когда получатель впервые получает данные из топика – для него создается индивидуальная пустая очередь.
 * Все последующие сообщения от отправителей с данными для этого топика помещаются в эту очередь тоже.
 * Таким образом в режиме "topic" для каждого потребителя будет своя уникальная очередь с данными,
 * в отличие от режима "queue", где для все потребители получают данные из одной и той же очереди.
 */
public class TopicService implements Service {
    private final ConcurrentMap<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topic =
            new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text = "";
        String status = "204";
        String type = req.httpRequestType();
        String key = req.getSourceName();
        String param = req.getParam();
        if ("POST".equals(type)) {
            /* Добавляем значение параметра в конец каждой индивидуальной очереди */
            topic.get(key).forEachValue(2, value -> value.add(param));
            status = "200";
        } else if ("GET".equals(type)) {
            /* Если нет значения для соответствующего ключа - создаем новый топик */
            topic.putIfAbsent(key, new ConcurrentHashMap<>());
            /* Если впервые получаем данные из топика – создаем индивидуальную пустую очередь под клиента */
            topic.get(key).putIfAbsent(param, new ConcurrentLinkedQueue<>());
            /* Сообщение забирается из начала индивидуальной очереди получателя и удаляется */
            Optional<String> value = Optional.ofNullable(topic.get(key).get(param).poll());
            if (value.isPresent()) {
                text = value.get();
                status = "200";
            }
        }
        return new Resp(text, status);
    }
}
