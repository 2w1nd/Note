package com.w1nd.rabbitmq_springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = RabbitmqSpringbootApplication.class)
@RunWith(SpringRunner.class)
public class TestRabbitMQ {
    // 注入rabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // hello world
    @Test
    public void testHello() {
        rabbitTemplate.convertAndSend("hello", "hello world");
    }

    @Test
    public void testWork() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("work", "hello world");
        }
    }

    // 广播u
    @Test
    public void testFanout() {
        rabbitTemplate.convertAndSend("logs", "", "Fanout的模型发送的消息");
    }

    @Test
    public void testRoute() {
        rabbitTemplate.convertAndSend("directs", "error", "发送error的key的路由信息");
    }

    // topic 动态路由
    @Test
    public void testTopic() {
        rabbitTemplate.convertAndSend("topics", "order.save", "user.save 路由消息");
    }
}
