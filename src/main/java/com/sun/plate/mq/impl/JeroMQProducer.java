package com.sun.plate.mq.impl;

import com.sun.plate.mq.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * Created by sun on 2017/9/20.
 */
public class JeroMQProducer implements Producer {

  private Logger logger = LoggerFactory.getLogger(JeroMQProducer.class);

  /**
   * MQ服务器地址
   */
  private String host;

  /**
   * 端口号
   */
  private int port ;

  /**
   *  IO线程数
   */
  private int ioThreads = 1;

  private ZMQ.Context context;
  private ZMQ.Socket publisher;


  /**
   * 初始化方法
   */
  public void init()
  {
    this.context = ZMQ.context(this.ioThreads);
    this.publisher = context.socket(ZMQ.REQ);
    this.publisher.connect("tcp://"+this.host+":"+this.port);
  }

  /**
   * 销毁资源
   */
  public void destory()
  {
    if(this.publisher != null)
    {
      this.publisher.close();
    }
    if(context != null)
    {
      context.term();
    }

  }


  public void send(String topic, String content) {

    try
    {

      boolean result = publisher.send(topic+content);
      String recv = publisher.recvStr();
      logger.info("发送消息：{}，接受回复;[]",topic+content,recv);

    }
    catch (Exception e)
    {
      logger.error("发送消息失败",e);
    }

  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getIoThreads() {
    return ioThreads;
  }

  public void setIoThreads(int ioThreads) {
    this.ioThreads = ioThreads;
  }
}
