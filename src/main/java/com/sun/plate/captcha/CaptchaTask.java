package com.sun.plate.captcha;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sun on 2017/10/16.
 */
public class CaptchaTask {

  /**
   * 验证码
   */
  private BufferedImage captcha;
  /**
   * 验证码Token
   */
  private String captchaToken;
  /**
   * 是否延迟等待返回
   */
  private boolean isDelayed;
  /**
   * 延迟等待时间
   */
  private long delayTime;

  private List<ChannelParam> channels = new ArrayList<ChannelParam>();

  public BufferedImage getCaptcha() {
    return captcha;
  }

  public void setCaptcha(BufferedImage captcha) {
    this.captcha = captcha;
  }

  public String getCaptchaToken() {
    return captchaToken;
  }

  public void setCaptchaToken(String captchaToken) {
    this.captchaToken = captchaToken;
  }

  public boolean isDelayed() {
    return isDelayed;
  }

  public void setDelayed(boolean delayed) {
    isDelayed = delayed;
  }

  public long getDelayTime() {
    return delayTime;
  }

  public void setDelayTime(long delayTime) {
    this.delayTime = delayTime;
  }

  public List<ChannelParam> getChannels() {
    return channels;
  }

  public void setChannels(List<ChannelParam> channels) {
    this.channels = channels;
  }
}
