package com.sun.plate.captcha.channel.zhima.support;

/**
 * Created by sun on 2017/10/28.
 */
public enum RequestType {
  RECOGNIZE("recognize", "识别"), REPORT_ERROR("report_error", "报错"), QUERY_BALANCE("query_balance",
      "查询余额");

  private String key;// 关键词

  private String value;// 说明值

  private RequestType(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

}
