package com.gp12713.rpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.gp12713.rpc")
public class SpringConfig {
    @Bean(name = "gpRpcServer")
    public GpRpcServer gpRpcServer(){return new GpRpcServer(8080);}
}
