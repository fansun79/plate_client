package com.sun.plate.captcha;

import com.sun.plate.captcha.channelhanlder.CaptchaChannelResult;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sun on 2017/10/16.
 */
public class CaptchaResult {

  private String result;

  private long time;

  private String channel;

  private List<CaptchaChannelResult> results = new LinkedList<CaptchaChannelResult>();


  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public List<CaptchaChannelResult> getResults() {
    return results;
  }

  public void setResults(List<CaptchaChannelResult> results) {
    this.results = results;
  }
}
