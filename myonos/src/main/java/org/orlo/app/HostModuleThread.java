package org.orlo.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HostModuleThread implements Runnable {
    @Override
    public void run() {
        System.out.println("----------------开启Host服务-------------------------");
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        ExecutorService executorService = Executors.newFixedThreadPool(25);
        ByteBuffer buffer = ByteBuffer.allocate(2048);
         try {
             serverSocketChannel = ServerSocketChannel.open();
             serverSocketChannel.bind(new InetSocketAddress(1026));
             serverSocketChannel.configureBlocking(false);
             selector = Selector.open();
             serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

             while (selector.select() > 0) {
                 Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                 while (iterator.hasNext()) {
                     SelectionKey next = iterator.next();
                     iterator.remove();
                     if (next.isAcceptable()) {
                         SocketChannel accept = serverSocketChannel.accept();
                         accept.configureBlocking(false);
                         accept.register(selector, SelectionKey.OP_READ);
                     }
                     else if (next.isReadable()) {
                         SocketChannel channel = (SocketChannel) next.channel();
                         int len = 0;
                         String res = "";
                         while ((len = channel.read(buffer)) > 0) {
                             buffer.flip();
                             res = new String(buffer.array(), 0, len);
                             buffer.clear();
                             System.out.println(res);
                             ClassifyModuleThread classifyModuleThread = new ClassifyModuleThread(res);
                             executorService.submit(classifyModuleThread);
                         }
                     }
                 }

             }
         } catch (IOException e) {
             e.printStackTrace();
         }finally {
             executorService.shutdown();
             try {
                 selector.close();
                 serverSocketChannel.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
    }
}
