package com.sun.plate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.sun.plate.entity.xnative.XRect;
import com.sun.plate.mq.impl.JeroMQProducer;
import com.sun.plate.mq.impl.JeroMQServer;
import com.sun.plate.mq.message.StartAutoPriceCmd;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by sun on 2017/10/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes ={PlateClientApplication.class})
public class AutoPriceServiceTest {

  @BeforeClass
  public static void beforeClass()
  {
  }

  @Test
  public void test()throws Exception
  {

    JeroMQProducer producer = new JeroMQProducer();
    producer.setHost("127.0.0.1");
    producer.setPort(6000);
    producer.setIoThreads(10);
    producer.init();

    StartAutoPriceCmd startAutoPriceCmd = new StartAutoPriceCmd();
    Set<String> set = new HashSet<String>();
    set.add("sun123");
    startAutoPriceCmd.setClientIds(set);
    startAutoPriceCmd.setInterval(1000);
    startAutoPriceCmd.setDuration(10000);
    startAutoPriceCmd.setRect(new XRect(1,1,1,1));
    String json = startAutoPriceCmd.toJSONString();
    producer.send("TOPIC_CMD_PRICE",json);

    Thread.sleep(5000);

  }
}
