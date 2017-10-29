package com.sun.plate.captcha.channelhanlder;

/**
 * Created by sun on 2017/10/23.
 */
public class CaptchaChannelResult {

  private String result;

  private String channel;

  private long time;

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }
}
