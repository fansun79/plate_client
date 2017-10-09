package com.sun.plate.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.sun.plate.mq.Consumer;
import com.sun.plate.mq.MsgListener;
import com.sun.plate.mq.Producer;
import com.sun.plate.mq.impl.JeroMQConsumer;
import com.sun.plate.mq.impl.JeroMQProducer;
import com.sun.plate.mq.listener.AutoPriceListener;
import com.sun.plate.mq.message.PriceMessage;
import com.sun.plate.mq.message.StartAutoPriceCmd;
import com.sun.plate.mq.message.StopAutoPriceCmd;
import com.sun.plate.service.AutoPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * Created by sun on 2017/10/6.
 */
@Configuration
public class MQConfig {

  private static final Logger logger = LoggerFactory.getLogger(MQConfig.class);

  static
  {
    ParserConfig.getGlobalInstance().addAccept(StartAutoPriceCmd.class.getName());
    ParserConfig.getGlobalInstance().addAccept(StopAutoPriceCmd.class.getName());
    ParserConfig.getGlobalInstance().addAccept(PriceMessage.class.getName());

  }

  @Value("${plate.client.mq.host}")
  private String mqHost;

  @Value("${plate.client.mq.p_port}")
  private int producerPort;

  @Value("${plate.client.mq.c_port}")
  private int consumerPort;

  @Autowired
  private AutoPriceListener autoPriceListener;

  @Bean(initMethod = "init",destroyMethod = "destory")
  public Producer mqProducer()
  {
    JeroMQProducer producer = new JeroMQProducer();
    producer.setHost(this.mqHost);
    producer.setPort(this.producerPort);
    producer.setIoThreads(50);
    return producer;
  }

  @Bean
  public Consumer autoPriceConsumer()
  {
    JeroMQConsumer autoPriceConsumer = new JeroMQConsumer();
    autoPriceConsumer.setHost(this.mqHost);
    autoPriceConsumer.setPort(this.consumerPort);
    autoPriceConsumer.setThreads(10);
    autoPriceConsumer.init();
    autoPriceConsumer.subscribe("TOPIC_CMD_PRICE", autoPriceListener);
    return autoPriceConsumer;
  }

}
