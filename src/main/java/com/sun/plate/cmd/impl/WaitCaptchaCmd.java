package com.sun.plate.cmd.impl;

import com.sun.plate.cmd.AbstractCommand;
import com.sun.plate.cmd.CommandSession;
import com.sun.plate.entity.xnative.XPoint;
import com.sun.plate.entity.xnative.XRect;
import com.sun.plate.ocr.MyImageRecognizer;
import com.sun.plate.util.MMCQ;
import com.sun.plate.util.jna.NativeTool;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 等待验证码加载，如果是等待验证码的话，自动点击
 * Created by sun on 2017/10/11.
 */
public class WaitCaptchaCmd extends AbstractCommand {

  private static final Logger logger = LoggerFactory.getLogger(WaitCaptchaCmd.class);

  private MyImageRecognizer myR = new MyImageRecognizer();

  private XRect captchaRect = null;  //验证码所在区域坐标

  private XPoint reloadPoint = null;  //刷新验证码点击点坐标

  private CheckType checkType = CheckType.CARDOCR; //扫描验证码的类型

  private String scanColors = "12/8";

  private int channelTimeout = 60; //识别渠道超时时间

  public WaitCaptchaCmd() {
  }

  public WaitCaptchaCmd(XRect captchaRect, XPoint reloadPoint, CheckType checkType) {
    this.captchaRect = captchaRect;
    this.reloadPoint = reloadPoint;
    this.checkType = checkType;
  }

  public XRect getCaptchaRect() {
    return captchaRect;
  }

  public void setCaptchaRect(XRect captchaRect) {
    this.captchaRect = captchaRect;
  }

  public XPoint getReloadPoint() {
    return reloadPoint;
  }

  public void setReloadPoint(XPoint reloadPoint) {
    this.reloadPoint = reloadPoint;
  }

  public CheckType getCheckType() {
    return checkType;
  }

  public void setCheckType(CheckType checkType) {
    this.checkType = checkType;
  }

  public String getScanColors() {
    return scanColors;
  }

  public void setScanColors(String scanColors) {
    this.scanColors = scanColors;
  }

//    public int getChannelTimeout() {
//        return channelTimeout;
//    }
//
//    public void setChannelTimeout(int channelTimeout) {
//        this.channelTimeout = channelTimeout;
//    }

  /**
   * 等待验证码加载
   */
  private boolean waitCaptchaByCardOCR(CommandSession session) throws Exception {
    throw new java.lang.UnsupportedOperationException("CardOCR已经不支持了");

  }

  private boolean waitCaptchaByColor(CommandSession session) throws Exception {
    /**
     * *  用区域颜色数量来确定验证码是否出现
     */
    int colors = 12;
    int expectColors = 8;
    if (StringUtils.isNotBlank(this.scanColors) && this.scanColors.contains("/")) {
      String ss[] = this.scanColors.split("/");
      colors = Integer.parseInt(ss[1]);
      expectColors = Integer.parseInt(ss[0]);
    }
    BufferedImage captchaImage = NativeTool
        .capture(this.getHwnd(), captchaRect.x, captchaRect.y, captchaRect.width,
            captchaRect.height);
    MMCQ.CMap result = MMCQ.computeMap(captchaImage, colors);
    String captchaContent = myR
        .recognizeImage(captchaImage, MyImageRecognizer.RECOGNIZE_TYPE_BUTTON, null);
    logger
        .debug("获取到颜色：{}, 内容：{}", result != null ? result.getBoxes().size() : "空", captchaContent);
//        System.out.println(colors+"-"+expectColors);
    if ((result == null || result.getBoxes().size() < expectColors || "reload"
        .equals(captchaContent) || "captchaing".equals(captchaContent))) {
      if ("reload".equals(captchaContent) && this.reloadPoint != null && !session
          .contains(CommandSession.IS_CLICK_REFRUSH)) {
        //点击刷新
        for (int i = 0; i < 3; i++) {
          NativeTool.click(this.getHwnd(), this.reloadPoint.x, this.reloadPoint.y);
          logger.debug("点击刷新校验码");
          this.info("点击刷新校验码");
          Thread.sleep(50);
        }
        session.add(CommandSession.IS_CLICK_REFRUSH, "true");
      }
      return false;
    } else {
      return true;
    }
  }

  @Override
  public boolean doExeute(CommandSession session) throws Exception {
    switch (this.checkType) {
      case CARDOCR:
        return this.waitCaptchaByCardOCR(session);
      case COLOR:
        return this.waitCaptchaByColor(session);
      default:
        logger.warn("不支持的等待类型：{}", checkType);
        return false;
    }
  }

  public static enum CheckType {
    CARDOCR, //依赖cardocr去解析图片
    COLOR; //依赖相关区域的颜色类型来解析图片
  }

  public static final void main(String[] args) throws Exception {
    MyImageRecognizer myR = new MyImageRecognizer();
    BufferedImage captchaImage = ImageIO.read(new File("d:\\captchaing.png"));
    MMCQ.CMap result = MMCQ.computeMap(captchaImage, 12);
    String captchaContent = myR
        .recognizeImage(captchaImage, MyImageRecognizer.RECOGNIZE_TYPE_BUTTON, null);
    logger
        .debug("获取到颜色：{}, 内容：{}", result != null ? result.getBoxes().size() : "空", captchaContent);
  }
}
