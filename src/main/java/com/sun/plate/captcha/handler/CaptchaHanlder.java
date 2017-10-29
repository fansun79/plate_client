package com.sun.plate.captcha.handler;

import com.sun.plate.captcha.CaptchaRequest;
import com.sun.plate.captcha.CaptchaResult;
import com.sun.plate.captcha.CaptchaTask;

/**
 * 验证码处理器
 * Created by sun on 2017/10/16.
 */
public interface CaptchaHanlder {

  /**
   * 接受验证码识别请求
   * @param reqest
   * @return
   */
  CaptchaTask accept(CaptchaRequest reqest);

  /**
   * 获取验证码识别结果，如果还没有计算出来，则会阻塞
   * @param task
   * @param timeout
   * @return
   */
  CaptchaResult getResult(CaptchaTask task,long timeout);

}
