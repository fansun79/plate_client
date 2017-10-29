package com.sun.plate.captcha.channel.zhima.support;

/**
 * Created by sun on 2017/10/28.
 */
public enum LogTypeEnum {

  YES(1), NO(0);

  private int type;

  private LogTypeEnum(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

}
