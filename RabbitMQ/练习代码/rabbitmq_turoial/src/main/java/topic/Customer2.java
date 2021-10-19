package topic;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;

public class Customer2 {
    public static void main(String[] args) throws IOException {
        // 获取连接
        Connection connection = RabbitMQUtils.getConnection();
        Channel channel = connection.createChannel();

        // 声明交换机以及交换机类型
        channel.exchangeDeclare("topics", "topic");
        // 创建一个临时队列
        String queue = channel.queueDeclare().getQueue();
        // 绑定队列和交换机，动态通配符
        channel.queueBind(queue, "topics", "user.#");
        // 消费消息
        channel.basicConsume(queue, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1：" + new String(body));
            }
        });
    }
}
