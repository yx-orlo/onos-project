package org.orlo.app;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 请求分类的线程，将收到的数据发送，并接收分类信息，将信息存储到concurrentLinkedQueue中.
 */
public class ClassifyModuleThread implements Runnable {
    private final String jsonString;
    private ConcurrentLinkedQueue<String> flowClq;
    public ClassifyModuleThread(String jsonString, ConcurrentLinkedQueue<String> flowClq) {
        this.jsonString = jsonString;
        this.flowClq = flowClq;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("192.168.137.1", 1025));
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            JsonNode jsonPart1 = jsonNode.get("specifier");
            JsonNode stats = jsonNode.get("stats");
            String sendString = "{\"stats\":" + stats.toString() + "}";
            byteBuffer.put(sendString.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            int len = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while ((len = socketChannel.read(byteBuffer)) > 0) {
                byteBuffer.flip();
                String res = new String(byteBuffer.array(), 0, len);
                byteBuffer.clear();
                stringBuilder.append(res);
            }
            String[] split = stringBuilder.toString().split(":");
            String jsonPart2 = split[1];
            JsonObject resJson = new JsonObject();
            resJson.set("specifier", jsonPart1.toString());
            resJson.set("res", jsonPart2);
//            System.out.println(resJson.toString());
            flowClq.offer(resJson.toString());
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
