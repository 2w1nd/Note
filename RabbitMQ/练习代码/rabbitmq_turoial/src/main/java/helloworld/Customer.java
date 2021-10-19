package helloworld;

import com.rabbitmq.client.*;
import org.junit.Test;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Customer {

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        // ConnectionFactory connectionFactory = new ConnectionFactory();
        // connectionFactory.setHost("192.168.9.3");
        // connectionFactory.setPort(5672);
        // connectionFactory.setVirtualHost("/ems");
        // connectionFactory.setUsername("ems");
        // connectionFactory.setPassword("123");
        //
        // // 创建连接对象
        // Connection connection = connectionFactory.newConnection();

        Connection connection = RabbitMQUtils.getConnection();

        // 创建通道
        Channel channel = connection.createChannel();

        // 通道绑定对象
        channel.queueDeclare("hello", false, false, false, null);

        // 消费消息
        // 参数1：消费哪个队列的消息，队列名称
        // 参数2：开始消息的自动确认机制
        // 参数3：消费时的回调接口
        channel.basicConsume("hello", true, new DefaultConsumer(channel) {
            // 最后一个参数：消息队列中取出的消息
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("new String(body) = " + new String(body));
            }
        });
        // channel.close();
        // connection.close();
    }
}
