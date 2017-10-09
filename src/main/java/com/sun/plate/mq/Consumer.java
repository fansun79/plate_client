package com.sun.plate.mq;

/**
 * Created by sun on 2017/9/20.
 */
public interface Consumer {

  void subscribe(String topic, MsgListener listener);

}
