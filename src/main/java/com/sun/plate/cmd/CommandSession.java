package com.sun.plate.cmd;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sun on 2017/10/10.
 */
public class CommandSession {

  public static final String CPATCHA_IMAGE_HASH = "captcha_hash";

  public static final String IS_CLICK_REFRUSH = "is_click_refresh";

  public static final String PRICE = "price";

  /**
   * 验证码异步识别器，可以为空
   */
//  private IAsyncCaptchaManager captchaManager = new DefaultAsyncCaptchaManager();

  /**
   * 会话参数
   */
  private ConcurrentHashMap<String, Object> parameters = new ConcurrentHashMap<String, Object>();


//  public CommandSession() {
//    this.captchaManager = null;
//  }
//
//  public CommandSession(IAsyncCaptchaManager captchaManager) {
//    this.captchaManager = captchaManager;
//  }

  public void add(String key, Object value) {
    this.parameters.put(key, value);
  }

  public Object get(String key) {
    return this.parameters.get(key);
  }

  public boolean contains(String key) {
    return this.parameters.containsKey(key);
  }

  public void remove(String key) {
    this.parameters.remove(key);
  }

  public void clear() {
//    this.captchaManager = new DefaultAsyncCaptchaManager();
    this.parameters.clear();
  }
//
//  public IAsyncCaptchaManager getCaptchaManager() {
//    return this.captchaManager;
//  }
//
//  public void setCaptchaManager(IAsyncCaptchaManager captchaManager) {
//    this.captchaManager = captchaManager;
//  }
}
