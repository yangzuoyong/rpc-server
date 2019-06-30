package com.gp12713.rpc;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class ProcessorHandler implements Runnable {
    private Socket socket;
    private Map<String,Object> handlerMap;

    public ProcessorHandler(Socket socket, Map<String, Object> handlerMap) {
        this.socket = socket;
        this.handlerMap = handlerMap;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = invoke(rpcRequest);
            objectOutputStream = new ObjectOutputStream((socket.getOutputStream()));
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if(null!=objectInputStream){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null!=objectOutputStream){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object invoke(RpcRequest rpcRequest) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //反射调用
        String serviceName = rpcRequest.getClassName();
        String version = rpcRequest.getVersion();
        //增加版本号的判断
        if(!StringUtils.isEmpty(version)){
            serviceName+="-"+version;
        }
        Object service = handlerMap.get(serviceName);
        if(null==service){
            throw  new RuntimeException("service not found:"+serviceName);
        }
        Object[] args = rpcRequest.getParameters();
        Method method=null;
        if(null!=args){
            //获取每个参数的类型
            Class<?>[] types = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            //跟去请求的类进行加载
            Class clazz = Class.forName(rpcRequest.getClassName());
            //sayHello, saveUser找到这个类中的方法
            method = clazz.getMethod(rpcRequest.getClassName(),types);
        } else {
            //跟去请求的类进行加载
            Class clazz=Class.forName(rpcRequest.getClassName());
            //sayHello, saveUser找到这个类中的方法
            method=clazz.getMethod(rpcRequest.getMethodName());
        }
        return method.invoke(service,args);
    }
}
