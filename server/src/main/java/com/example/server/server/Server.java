package com.example.server.server;

import com.example.server.MainServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {

    private final int port;
    private final int threads;
    private final int timeout;

    private final ExecutorService threadPool;

    private ServerSocket socket;

    public Server() {
        port = Integer.parseInt(MainServer.getModel().getConfig().getProperty("srv.port"));
        threads = Integer.parseInt(MainServer.getModel().getConfig().getProperty("srv.threads"));
        timeout = Integer.parseInt(MainServer.getModel().getConfig().getProperty("srv.timeout")) * 1000;

        threadPool = Executors.newFixedThreadPool(threads);
    }

    public void start() {
        super.start();
    }

    public void run() {
        MainServer.log(String.format("server started @%d threads:%d timeout:%d sec", port, threads, timeout/1000));

        try {
            socket = new ServerSocket(port);

            while (!Thread.interrupted()) {
                Socket connection = socket.accept();
                connection.setSoTimeout(timeout);
                threadPool.execute(new Task(connection));
            }
        }
        catch (SocketException se) {}
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        try {
            threadPool.shutdown();
            if(threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interrupt() {
        if (!socket.isClosed()) {
            close();
        }
        super.interrupt();
    }
}
