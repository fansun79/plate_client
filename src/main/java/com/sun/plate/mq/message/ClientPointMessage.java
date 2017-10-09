package com.sun.plate.mq.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 来自客户端的消息，表明自己的客户端ID
 * Created by sun on 2017/10/7.
 */
public abstract  class ClientPointMessage {

  private String clientId;

  private String clientIp;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public String toJSONString() {
    String json = JSON.toJSONString(this, SerializerFeature.WriteClassName,
        SerializerFeature.WriteMapNullValue  );
    return json;
  }

}
