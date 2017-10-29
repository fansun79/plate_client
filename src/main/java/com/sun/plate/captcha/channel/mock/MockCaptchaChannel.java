package com.sun.plate.captcha.channel.mock;

import com.sun.plate.captcha.CaptchaTask;
import com.sun.plate.captcha.channel.CaptchaChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sun on 2017/10/24.
 */
public class MockCaptchaChannel implements CaptchaChannel {

  private static final Logger logger = LoggerFactory.getLogger(MockCaptchaChannel.class);

  private String captchaContent;
  private long wait;

  public MockCaptchaChannel(String captchaContent, long wait) {
    this.captchaContent = captchaContent;
    this.wait = wait;
  }

  @Override
  public String getCaptcha(CaptchaTask task) {
    try {
      logger.debug("模拟获取验证码，内容：{}，休眠：{}", this.captchaContent, this.wait);
      if (this.wait > 0) {
        Thread.sleep(this.wait);
      }
      return this.captchaContent;
    } catch (InterruptedException e) {
      logger.debug("中断获取验证码");
      return this.captchaContent;
    }

  }

  @Override
  public String getChannelName() {
    return "mock";
  }
}
