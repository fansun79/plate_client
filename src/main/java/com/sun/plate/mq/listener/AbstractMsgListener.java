package com.sun.plate.mq.listener;

import com.alibaba.fastjson.JSON;
import com.sun.plate.entity.xnative.NativeInfo;
import com.sun.plate.mq.MsgListener;
import com.sun.plate.mq.message.ServerPointMessage;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sun on 2017/10/7.
 */
public  abstract  class AbstractMsgListener implements MsgListener{

  private static final Logger logger = LoggerFactory.getLogger(AbstractMsgListener.class);

  @Autowired
  private NativeInfo nativeInfo;

  @Override
  public void consume(String topic,String msg) {

     logger.warn("接受mq请求: topic:[{}],msg:[{}]",topic,msg);
     try
     {
       Object obj = JSON.parse(msg);
       if(obj instanceof ServerPointMessage)
       {
         ServerPointMessage serverPointMessage = (ServerPointMessage)obj;
         Set<String> clientIds = serverPointMessage.getClientIds();
         if(clientIds != null && !clientIds.isEmpty())
         {
            boolean isContains = clientIds.contains(this.nativeInfo.getClientId());
            if(!isContains)
            {
              logger.warn("不需要处理请求");
              return;
            }
         }
       }
       this.doConsume(obj);
     }
     catch (Exception e)
     {
       logger.warn("消息处理异常,topic:[{}],msg:[{}]",topic,msg,e);
     }

  }

  public abstract void doConsume(Object msg);
}
