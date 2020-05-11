package org.orlo.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClassifyModuleThread implements Runnable{
    private final String jsonString;

    public ClassifyModuleThread(String jsonString) {
        this.jsonString = jsonString;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("192.168.137.1", 1025));
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
            JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            JsonNode stats = jsonNode.get("stats");
            String sendString = "{\"stats\":" + stats.toString() + "}";
            byteBuffer.put(sendString.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    if (next.isReadable()) {
                        SocketChannel channel = (SocketChannel) next.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = 0;
                        String res = "";
                        while ((len = channel.read(buffer)) > 0) {
                            buffer.flip();
                            res = new String(buffer.array(), 0, len);
                            buffer.clear();
                        }
                        System.out.println(res);
                        System.out.println(Thread.currentThread() + "----------------下发流表--------------");
                        Thread.sleep(1000);
                    }
                }

            }
            selector.close();
            socketChannel.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
