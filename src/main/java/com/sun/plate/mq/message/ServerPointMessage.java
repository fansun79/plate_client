package com.sun.plate.mq.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Set;

/**
 * 来自server端的消息，会指定需要接受的客户端
 * Created by sun on 2017/10/7.
 */
public abstract class ServerPointMessage {

  /**
   * 指定需要处理消息的客户端，如果为空，所有客户端必须都要处理
   */
  private Set<String> clientIds;

  public Set<String> getClientIds() {
    return clientIds;
  }

  public void setClientIds(Set<String> clientIds) {
    this.clientIds = clientIds;
  }

  public String toJSONString() {
    String json = JSON.toJSONString(this, SerializerFeature.WriteClassName,
        SerializerFeature.WriteMapNullValue  );
    return json;
  }
}
