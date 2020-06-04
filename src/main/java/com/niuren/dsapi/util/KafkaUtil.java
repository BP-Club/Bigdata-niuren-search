package com.niuren.dsapi.util;


import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.List;
import java.util.Properties;


/**
 * Kafka工具类<br>
 * Kafka工具类，包括初始化连接，发送json数据
 *
 * @author 14062537
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class KafkaUtil {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaUtil.class);

    private ZkUtils zkUtils = null;
    private String zk_server = null;
    private String bootstrap_servers = null;
    private Producer<String, String> producer = null;

    public String getZk_server() {
        return zk_server;
    }

    public void setZk_server(String zk_server) {
        this.zk_server = zk_server;
    }

    public String getBootstrap_servers() {
        return bootstrap_servers;
    }

    public void setBootstrap_servers(String bootstrap_servers) {
        this.bootstrap_servers = bootstrap_servers;
    }

    public KafkaUtil(String bootstrap_servers, String zk_server) {
        this.zk_server = zk_server;
        this.bootstrap_servers = bootstrap_servers;
        buildConnect();
    }

    /**
     * Broker连接 <br>
     *
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private synchronized void buildConnect() {
        if (producer == null) {
            try {
                Properties props = new Properties();
                props.put("acks", "1");
                props.put("retries", 0);
                props.put("batch.size", 100);//达到最大批次发送
                props.put("linger.ms", 100);//100毫秒发送一次
                props.put("buffer.memory", 3000);
                props.put("bootstrap.servers", bootstrap_servers);
                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                producer = new KafkaProducer<>(props);
                LOG.info("**********init kafkaProducer client************");
            } catch (Exception e) {
                LOG.error("init kafkaProducer client error", e);
            }
        }

    }

    /**
     * 单条--同步发送消息(含分区) <br>
     *
     * @param topic   topic名称
     * @param message 消息体
     */
    public void sendMessage(String topic, String message) {
        try {
            buildConnect();
            // 数据发送
            if (StringUtils.isNotEmpty(message)) {
                producer.send(new ProducerRecord(topic, message));
            }
            LOG.debug("send topic: ", topic, " message:" + message);
        } catch (Exception e) {
            LOG.warn("send to kafka Failed:", e);
            // 重新连接
            buildConnect();
        }
    }

    /**
     * 创建Topic
     *
     * @return
     */
    public boolean createTopic(String topicName) {
        zkUtils = ZkUtils.apply(zk_server, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        try {
            if (!isTopicExists(topicName)) {
                AdminUtils.createTopic(zkUtils, topicName, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
                return true;
            }
        } catch (Exception e) {
            LOG.error("createTopic Failed:", e.getCause());
        }
        return false;
    }

    /**
     * 删除topic信息（前提是server.properties中要配置delete.topic.enable=true）
     *
     * @param topicName
     */
    public void deleteTopic(String topicName) {
        if (isTopicExists(topicName)) {
            zkUtils = ZkUtils.apply(zk_server, 30000, 30000, JaasUtils.isZkSecurityEnabled());
            // 删除topic 't1'
            AdminUtils.deleteTopic(zkUtils, topicName);
            LOG.info("deleteTopic ok !");
        }
    }

    /**
     * 改变topic的配置
     *
     * @param topicName
     */
    public void updateTopic(String topicName) {
        zkUtils = ZkUtils.apply(zk_server, 30000, 30000, JaasUtils.isZkSecurityEnabled());

        Properties props = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), "test");
        // 增加topic级别属性
        props.put("min.cleanable.dirty.ratio", "0.3");
        // 删除topic级别属性
        props.remove("max.message.bytes");
        props.put("retention.ms", "1000");
        // 修改topic 'test'的属性
        AdminUtils.changeTopicConfig(zkUtils, topicName, props);
        System.out.println("修改成功");
        zkUtils.close();
    }

    /**
     * 判断某个topic是否存在
     *
     * @param topicName
     * @return
     */
    public boolean isTopicExists(String topicName) {
        zkUtils = ZkUtils.apply(zk_server, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        boolean exists = AdminUtils.topicExists(zkUtils, topicName);
        return exists;
    }


    /**
     * 获取所有的TopicList
     *
     * @return
     */
    public List<String> getTopicList() {
        zkUtils = ZkUtils.apply(zk_server, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        List<String> allTopicList = JavaConversions.seqAsJavaList(zkUtils.getAllTopics());
        return allTopicList;
    }

    //执行
    public void main(String args[]) {
        //        createTopic("zu-test");
        //        deleteTopic("zu-test");
        //        List<String> topicList = getTopicList();
        //        for (String topic : topicList) {
        //            System.out.println(topic);
        //        }
        //        System.out.println(JSON.toJSONString(topicList));
    }
}