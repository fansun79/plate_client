//package com.sun.plate.cmd.impl;
//
//import com.sun.plate.cmd.AbstractCommand;
//import com.sun.plate.cmd.CommandSession;
//import com.sun.plate.support.xnative.XRect;
//import com.sun.plate.util.PlateUtil;
//import com.sun.plate.util.jna.NativeTool;
//import java.awt.image.BufferedImage;
//import java.util.HashMap;
//import java.util.Map;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Created by sun on 2017/10/10.
// */
//public class ReadingCaptchaCmd  extends AbstractCommand{
//
//  private static final Logger logger = LoggerFactory.getLogger(ReadingCaptchaCmd.class);
//
//  /**
//   *  验证码页面区域
//   */
//  private XRect captchaRect = null;
//  /**
//   *  验证码规则页面区域
//   */
//  private XRect ruleRect = null;
//  /**
//   *  伸缩尺寸
//   */
//  private Integer scale = null;
//  /**
//   *  图片亮度
//   */
//  private Integer lightness = null;
//  /**
//   * 色调
//   */
//  private Integer hue = null;
//  /**
//   * 饱和度
//   */
//  private Integer saturation = null;
//  /**
//   *  验证码Token 用于标志此次获取验证码会话
//   */
//  private String token = null;
//  /**
//   *  可用的打码渠道
//   */
//  private Map<String,String> captchaChannels = new HashMap<String,String>();
//  /**
//   *  验证码长度
//   */
//  private int captchaLength = 4;
//  /**
//   *  指定有几个相同答案，才确定验证码
//   */
//  private int answerNum = 2;
//  /**
//   *  验证码类型
//   */
//  private OCRType ocrType = OCRType.Number;
//  /**
//   *   是否保存中间图片
//   */
//  private boolean debug = false;
//
//  /**
//   * 识别渠道超时时间
//   */
//  private int channelTimeout = 60;
//
//
//  public XRect getCaptchaRect() {
//    return captchaRect;
//  }
//
//  public void setCaptchaRect(XRect captchaRect) {
//    this.captchaRect = captchaRect;
//  }
//
//  public XRect getRuleRect() {
//    return ruleRect;
//  }
//
//  public void setRuleRect(XRect ruleRect) {
//    this.ruleRect = ruleRect;
//  }
//
//  public Integer getScale() {
//    return scale;
//  }
//
//  public void setScale(Integer scale) {
//    this.scale = scale;
//  }
//
//  public Integer getLightness() {
//    return lightness;
//  }
//
//  public void setLightness(Integer lightness) {
//    this.lightness = lightness;
//  }
//
//  public Integer getHue() {
//    return hue;
//  }
//
//  public void setHue(Integer hue) {
//    this.hue = hue;
//  }
//
//  public Integer getSaturation() {
//    return saturation;
//  }
//
//  public void setSaturation(Integer saturation) {
//    this.saturation = saturation;
//  }
//
//  public String getToken() {
//    return token;
//  }
//
//  public void setToken(String token) {
//    this.token = token;
//  }
//
//  public Map<String,String> getCaptchaChannels() {
//    return captchaChannels;
//  }
//
//  public int getCaptchaLength() {
//    return captchaLength;
//  }
//
//  public void setCaptchaLength(int captchaLength) {
//    this.captchaLength = captchaLength;
//  }
//
//  public int getAnswerNum() {
//    return answerNum;
//  }
//
//  public void setAnswerNum(int answerNum) {
//    this.answerNum = answerNum;
//  }
//
//  public void setCaptchaChannels(Map<String,String> captchaChannels) {
//    this.captchaChannels = captchaChannels;
//  }
//
//  public OCRType getOcrType() {
//    return ocrType;
//  }
//
//  public void setOcrType(OCRType ocrType) {
//    this.ocrType = ocrType;
//  }
//
//  public boolean isDebug() {
//    return debug;
//  }
//
//  public void setDebug(boolean debug) {
//    this.debug = debug;
//  }
//
//  public int getChannelTimeout() {
//    return channelTimeout;
//  }
//
//  public void setChannelTimeout(int channelTimeout) {
//    this.channelTimeout = channelTimeout;
//  }
//
//  /**
//   *  计算图片的hash值
//   * @param image
//   * @return
//   */
//  protected  String  getBufferedImageHash(BufferedImage image)
//  {
//    byte[] imageData = PlateUtil.getBytes(image); //计算字节数组
//    return PlateUtil.MD5(imageData);
//  }
//
//  @Override
//  public boolean doExeute(CommandSession session) throws Exception {
//
//    /**
//     *  识别验证码
//     */
//    BufferedImage captchaImage = NativeTool.capture(this.getHwnd(), this.captchaRect.x, this.captchaRect.y,
//        this.captchaRect.width, this.captchaRect.height);
//    BufferedImage ruleImage = NativeTool.capture(this.getHwnd(), this.ruleRect.x, this.ruleRect.y,
//        this.ruleRect.width, this.ruleRect.height);
//
//    DefaultAsyncCaptchaManager captchaManager = (DefaultAsyncCaptchaManager) session.getCaptchaManager();
//    if (captchaManager != null && captchaManager.containsImageHash(captchaImage, ruleImage)) {
////            logger.debug("验证码已存在");
//      this.info("验证码已提交验证");
//      return true;
//    } else {
////            logger.debug("验证码不存在");
//      this.info("验证码正提交验证");
//      captchaManager = this.createCaptchaManager();
//      session.setCaptchaManager(captchaManager);
//      String hash = captchaManager.startOCR(captchaImage, ruleImage);
////            logger.debug("hash:{}",hash);
////            logger.debug("提交OCR结束");
//      session.add(CommandSession.CPATCHA_IMAGE_HASH, hash);
////            logger.debug("hash写入session");
//      return true;
//    }
//
//  }
//
//  private DefaultAsyncCaptchaManager createCaptchaManager()
//  {
//    DefaultAsyncCaptchaManager captchaManager = new DefaultAsyncCaptchaManager();
//    captchaManager.setScale(this.scale);
//    captchaManager.setSaturation(this.saturation);
//    captchaManager.setHue(this.hue);
//    captchaManager.setLightness(this.lightness);
//    captchaManager.setOcrType(this.ocrType);
//    captchaManager.setCaptchaNum(this.captchaLength);
////        captchaManager.setAnswerNum(this.answerNum);
//    captchaManager.captchaChannels.clear();
//    captchaManager.captchaChannels.putAll(this.captchaChannels);
//    captchaManager.setIsDebug(this.debug);
//    captchaManager.setTimeout(this.channelTimeout);
//    captchaManager.setsLogger(this.getStrategyLogger());
//    return captchaManager;
//  }
//
//}
