package com.lwh.helloword;

import com.lwh.helloword.server.EchoClient;
import com.lwh.helloword.server.EchoServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HelloWordApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWordApplication.class, args);
    }

    @Bean
    public CommandLineRunner startTask() {
        return (args) -> {
            new Thread(() -> {
                try {
                    new EchoServer(8081).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            Thread.sleep(3000L);
            new Thread(() -> {
                try {
                    new EchoClient("127.0.0.1", 8081).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        };
    }

}
