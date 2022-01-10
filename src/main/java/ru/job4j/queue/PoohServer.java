package ru.job4j.queue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Сервер.
 */
public class PoohServer {
    private final Map<String, Service> modes = new HashMap<>();

    public void start() {
        modes.put("queue", new QueueService());
        modes.put("topic", new TopicService());
        /* Создаем пул потоков */
        ExecutorService pool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        /* Создаем серверный сокет с портом 9000 */
        try (ServerSocket server = new ServerSocket(9000)) {
            /* Сервер работает пока не будет принудительно закрыт */
            while (!server.isClosed()) {
                /* Создаем клиентский сокет и ждем к нему подключение клиента */
                Socket socket = server.accept();
                /* Запускаем выполнение потока */
                pool.execute(() -> {
                    /* Создаем два стрима: один на чтение второй на запись */
                    try (OutputStream out = socket.getOutputStream();
                         InputStream input = socket.getInputStream()) {
                        /* Считываем информацию массивом байтов и преобразуем в строку */
                        byte[] buff = new byte[1_000_000];
                        var total = input.read(buff);
                        var content = new String(Arrays.copyOfRange(buff, 0, total), StandardCharsets.UTF_8);
                        /* Парсим полученную информацию */
                        var req = Req.of(content);
                        /* Формируем ответ сервера */
                        var resp = modes.get(req.getPoohMode()).process(req);
                        String ls = System.lineSeparator();
                        /* Отправляем ответ сервера */
                        out.write(("HTTP/1.1 " + resp.status() + ls).getBytes());
                        out.write((resp.text().concat(ls)).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        new PoohServer().start();
    }
}
