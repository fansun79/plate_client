package com.sun.plate.captcha;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by sun on 2017/10/26.
 */
public class ChannelParam {

  private String channelName;

  private Map<String,String> params;

  public ChannelParam(){}

  public ChannelParam(String channelName,Pair<String,String>... params)
  {
     this.channelName = channelName;
     this.params = new HashMap<String, String>();
     for(Pair<String,String> param : params)
     {
        this.params.put(param.getLeft(),param.getRight());
     }
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }
}
