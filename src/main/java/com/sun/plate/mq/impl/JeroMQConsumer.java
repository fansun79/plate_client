package com.sun.plate.mq.impl;

import com.sun.plate.mq.Consumer;
import com.sun.plate.mq.MsgListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.zeromq.ZMQ;

/**
 * Created by sun on 2017/9/21.
 */
public class JeroMQConsumer implements Consumer {

  private String host;

  private int port;

  private int threads = 5;

  private String topic;

  private MsgListener listener;

  private ExecutorService threadPool;

  private ZMQ.Context context;

  private ZMQ.Socket subscriber;

  private Thread thread;


  public void init()
  {
      this.threadPool = Executors.newFixedThreadPool(threads);
      this.context = ZMQ.context(5);
      this.subscriber = context.socket(ZMQ.SUB);
      this.subscriber.connect("tcp://"+host+":"+port);

  }

  public void destory()
  {
     this.thread.interrupt();
     this.threadPool.shutdown();
     if(this.subscriber != null)
     {
       this.subscriber.close();
     }
     if(this.context != null)
     {
       this.context.term();
     }
  }


  public void subscribe(final String topic, final MsgListener listener) {
    this.topic = topic;
    this.listener = listener;
    this.subscriber.subscribe(topic);
    this.thread = new Thread(new Runnable() {
      public void run() {
        while (!Thread.currentThread().isInterrupted())
        {
          String recvStr = subscriber.recvStr();
          if(recvStr.startsWith(topic))
          {
            recvStr = recvStr.substring(topic.length(),recvStr.length());
          }
          threadPool.execute(new ListenerThread(listener,recvStr,topic));
        }
      }
    });
    this.thread.start();


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

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  private class ListenerThread implements  Runnable
  {
    private MsgListener listener;

    private String msg;

    private String topic;

    public ListenerThread(MsgListener listener,String msg,String topic)
    {
      this.listener = listener;
      this.msg = msg;
      this.topic = topic;
    }

    public void run() {
        this.listener.consume(this.topic,this.msg);
    }
  }




}
