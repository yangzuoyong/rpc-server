package com.gp12713.rpc;
@RpcService(value=IHelloService.class,version="v2.0")
public class HelloServiceImpl2 implements IHelloService {
    @Override
    public String sayHello(String content) {
        System.out.println("【V1.0】request in sayHello:"+content);
        return "【V2.0】Say Hello:"+content;
    }

    @Override
    public String saveUser(User user) {
        System.out.println("【V1.0】request in saveUser:"+user);
        return "【V2.0】SUCCESS";
    }
}
