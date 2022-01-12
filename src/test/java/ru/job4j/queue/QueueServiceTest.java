package ru.job4j.queue;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class QueueServiceTest {

    @Test
    public void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        /* Добавляем данные в очередь weather. Режим queue */
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        /* Забираем данные из очереди weather. Режим queue */
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("temperature=18"));
    }

    @Test
    public void whenNotPostAndGetQueue() {
        QueueService queueService = new QueueService();
/*        String paramForPostMethod = "temperature=18";
        *//* Добавляем данные в очередь weather. Режим queue *//*
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );*/
        /* Забираем данные из очереди weather ничего не добавив в неё. Режим queue */
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is(""));
    }
}