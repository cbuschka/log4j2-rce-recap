import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

public class LdapMock {

    private final AtomicInteger counter = new AtomicInteger(0);

    private volatile ServerSocket serverSocket;

    public synchronized void start() throws IOException {
        serverSocket = new ServerSocket(1389, 5, InetAddress.getByName("127.0.0.1"));
        new Thread(this::run).start();
    }

    public int getConnectionCount() {
        return counter.get();
    }

    public void resetConnectionCount() {
        counter.set(0);
    }

    @SneakyThrows(IOException.class)
    private void run() {
        while (serverSocket != null) {
            try (Socket socket = serverSocket.accept()) {
                counter.incrementAndGet();
            } catch (SocketException ex) {
                boolean regularShutdown = serverSocket == null;
                if (!regularShutdown) {
                    throw ex;
                }
            }
        }
    }

    @SneakyThrows(IOException.class)
    public synchronized void stop() {
        ServerSocket oldServerSocket = serverSocket;
        serverSocket = null;

        oldServerSocket.close();
    }
}
