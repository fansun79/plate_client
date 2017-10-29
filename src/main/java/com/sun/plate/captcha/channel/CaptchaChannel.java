package com.sun.plate.captcha.channel;

import com.sun.plate.captcha.CaptchaTask;

/**
 * 验证码渠道
 * Created by sun on 2017/10/16.
 */
public interface CaptchaChannel {

  /**
   * 获取验证码
   * @param task
   * @return
   */
  String getCaptcha(CaptchaTask task);


  String getChannelName();

}
