package com.sun.plate.cmd.impl;

import com.sun.plate.cmd.AbstractCommand;
import com.sun.plate.cmd.CommandSession;
import com.sun.plate.entity.xnative.XRect;
import com.sun.plate.ocr.MyImageRecognizer;
import com.sun.plate.util.jna.NativeTool;
import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sun on 2017/10/11.
 */
public class WaitFrameCmd extends AbstractCommand {

  private Logger logger = LoggerFactory.getLogger(WaitFrameCmd.class);

  private FrameType frameType; //窗体类型

  private MyImageRecognizer myR = new MyImageRecognizer(); //OCR识别

  private XRect frameFlag = null;   //窗体标志区域

  public WaitFrameCmd() {
  }

  public WaitFrameCmd(FrameType frameType, XRect frameFlag) {
    this.frameType = frameType;
    this.frameFlag = frameFlag;
  }

  public FrameType getFrameType() {
    return frameType;
  }

  public void setFrameType(FrameType frameType) {
    this.frameType = frameType;
  }

  public XRect getFrameFlag() {
    return frameFlag;
  }

  public void setFrameFlag(XRect frameFlag) {
    this.frameFlag = frameFlag;
  }

  @Override
  public boolean doExeute(CommandSession session) throws Exception {

    BufferedImage image;
    String result = null;
    switch (frameType) {
      case CAPTCHA_FRAME:
        image = NativeTool
            .capture(this.getHwnd(), frameFlag.x, frameFlag.y, frameFlag.width, frameFlag.height);
        result = myR.recognizeImage(image, "confirm");
        return result.trim().startsWith("ok");
      case FOLLOWING_CAPTCHA_FRAME:
        image = NativeTool
            .capture(this.getHwnd(), frameFlag.x, frameFlag.y, frameFlag.width, frameFlag.height);
        result = myR.recognizeImage(image, "confirm");
        return result.trim().startsWith("ok");
      case NO_CAPTCHA_FRAME:
        image = NativeTool
            .capture(this.getHwnd(), frameFlag.x, frameFlag.y, frameFlag.width, frameFlag.height);
        result = myR.recognizeImage(image, "confirm");
        return !result.trim().startsWith("ok");
      case QUOTE_FRAME:
        image = NativeTool
            .capture(this.getHwnd(), frameFlag.x, frameFlag.y, frameFlag.width, frameFlag.height);
        result = myR.recognizeImage(image, "button");
//                System.out.println(result);
        return result.trim().startsWith("quote");
      default:
        logger.warn("不支持的窗体类型：{}", frameType);
        return false;
    }

  }


  public static enum FrameType {
    CAPTCHA_FRAME, //输入验证码窗体
    FOLLOWING_CAPTCHA_FRAME,//输入验证码后窗体
    NO_CAPTCHA_FRAME, //非验证码窗体
    QUOTE_FRAME;  //出价窗体
  }
}
