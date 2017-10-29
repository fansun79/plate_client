package com.sun.plate.captcha;

import java.awt.image.BufferedImage;

/**
 * 验证码请求
 * Created by sun on 2017/10/16.
 */
public class CaptchaRequest {

  /**
   * 验证码
   */
  private BufferedImage captcha;
  /**
   * 验证码提示
   */
  private BufferedImage prompt;
  /**
   * 是否等待所有结果返回，还是有结果直接返回
   */
  private boolean isDelayed;
  /**
   * 延迟获取验证码时间，单位毫秒
   */
  private long delayedTime;

  public BufferedImage getCaptcha() {
    return captcha;
  }

  public void setCaptcha(BufferedImage captcha) {
    this.captcha = captcha;
  }

  public BufferedImage getPrompt() {
    return prompt;
  }

  public void setPrompt(BufferedImage prompt) {
    this.prompt = prompt;
  }

  public boolean isDelayed() {
    return isDelayed;
  }

  public void setDelayed(boolean delayed) {
    isDelayed = delayed;
  }

  public long getDelayedTime() {
    return delayedTime;
  }

  public void setDelayedTime(long delayedTime) {
    this.delayedTime = delayedTime;
  }
}
