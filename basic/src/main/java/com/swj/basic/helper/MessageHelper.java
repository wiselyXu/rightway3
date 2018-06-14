package com.swj.basic.helper;

import com.swj.basic.SwjConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * com.swj.basic.helper
 *
 * @autor Chenjw
 * @since 2018/5/22
 **/
public class MessageHelper {
    private static Properties props;
    private static Producer<String, Object> producer;
    private static final String BOOTSTRAP_SERVERS = "bootstrap-servers";

    static {
        props = new Properties();
        props.put("bootstrap.servers", SwjConfig.get(BOOTSTRAP_SERVERS));
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, Object>(props);
    }

    /**
     * 向主题发送消息
     *
     * @author Chenjw
     * @since 2018/5/22
     **/
    public static void sentMessage(String topic, Object value) {
        ProducerRecord<String, Object> msg = new ProducerRecord<String, Object>(topic, JsonHelper.toJsonString(value));
        producer.send(msg);
    }
}
