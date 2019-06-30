package com.gp12713.rpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcProxyServer {

    ExecutorService executorService= Executors.newCachedThreadPool();

    public void publisher(Object service,int port){

        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(port);
            while(true){//不断接受请求
                Socket socket=serverSocket.accept();//BIO
                Map<String, Object> handlerMap = new HashMap<>();
                handlerMap.put("",service);
                //每一个socket 交给一个processorHandler来处理
                executorService.execute(new ProcessorHandler(socket,handlerMap));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
