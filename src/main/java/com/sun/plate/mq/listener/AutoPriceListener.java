package com.sun.plate.mq.listener;

import com.sun.plate.mq.message.StartAutoPriceCmd;
import com.sun.plate.mq.message.StopAutoPriceCmd;
import com.sun.plate.service.AutoPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sun on 2017/10/7.
 */
@Component
public class AutoPriceListener extends AbstractMsgListener{

  @Autowired
  private AutoPriceService autoPriceService;

  @Override
  public void doConsume(Object msg) {

    if(msg instanceof StartAutoPriceCmd)
    {
      StartAutoPriceCmd message = (StartAutoPriceCmd)msg;
       this.autoPriceService.start(message.getInterval(),message.getDuration(),message.getRect());
    }
    else if(msg instanceof StopAutoPriceCmd)
    {
      this.autoPriceService.stop();
    }
    else
    {
      throw new java.lang.UnsupportedOperationException("不支持的消息类型");
    }
  }
}
