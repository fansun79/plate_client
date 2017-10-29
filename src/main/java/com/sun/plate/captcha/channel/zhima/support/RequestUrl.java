package com.sun.plate.captcha.channel.zhima.support;

/**
 * Created by sun on 2017/10/28.
 */
public enum RequestUrl {

  HTTP_API_URL("http://ff.zhima365.com/zmdemo_php/http_api.php");
  private String url;

  private RequestUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

}