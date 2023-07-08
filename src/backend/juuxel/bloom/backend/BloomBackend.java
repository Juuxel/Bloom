package juuxel.bloom.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import juuxel.bloom.backend.codec.Result;
import juuxel.bloom.backend.message.Message;
import juuxel.bloom.backend.response.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class BloomBackend implements Runnable {
    private final BloomEngine engine;
    private final BlockingQueue<Message> messageQueue;

    private BloomBackend(Gson gson, BlockingQueue<Message> messageQueue) {
        this.engine = new BloomEngine(gson);
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                break;
            }

            Message message;

            try {
                message = messageQueue.take();
            } catch (InterruptedException e) {
                break;
            }

            if (message.shouldExit()) {
                System.err.println("goodbye!");
                break;
            }

            message.run(engine);
        }
    }

    public static void main(String[] args) throws IOException {
        var gson = new Gson();
        var messageQueue = new LinkedBlockingQueue<Message>();
        var backend = new BloomBackend(gson, messageQueue);
        var backendThread = new Thread(backend, "Processing thread");
        backendThread.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JsonElement json = gson.fromJson(line, JsonElement.class);
                Communicable c = Communicable.CODEC.decode(json).orElseThrow(JsonSyntaxException::new);

                if (c instanceof Message m) {
                    messageQueue.offer(m);
                    if (m.shouldExit()) break;
                } else {
                    System.err.println("Not a message to the backend: " + c);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        backendThread.interrupt();
    }
}
