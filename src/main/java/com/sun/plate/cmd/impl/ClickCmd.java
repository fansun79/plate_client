package com.sun.plate.cmd.impl;

import com.alibaba.fastjson.JSON;
import com.sun.jna.platform.win32.WinDef;
import com.sun.plate.cmd.AbstractCommand;
import com.sun.plate.cmd.CommandSession;
import com.sun.plate.util.jna.JNativeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 点击命令
 * Created by sun on 2017/10/10.
 */
public class ClickCmd extends AbstractCommand {

  private final static Logger logger = LoggerFactory.getLogger(ClickCmd.class);

  private int x;

  private int y;

  private boolean isTest;

  public ClickCmd() {
  }

  public ClickCmd(int x, int y) {
    this.x = x;
    this.y = y;
    this.isTest = false;
  }

  public ClickCmd(int x, int y, boolean isTest) {
    this.x = x;
    this.y = y;
    this.isTest = isTest;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public boolean isTest() {
    return isTest;
  }

  public void setIsTest(boolean isTest) {
    this.isTest = isTest;
  }

  @Override
  public boolean doExeute(CommandSession session) throws Exception {

    if (isTest) {
      logger.info("模拟点击命令，{}", JSON.toJSONString(this));
      this.info("模拟点击命令");
      return true;
    }
//            logger.info("点击x:{},y:{}",x,y);
    JNativeUtils.mouseClick(this.getHwnd(), x, y, isTest());
    return false;
  }

  public static final void main(String[] args) throws Exception {
    WinDef.HWND hwnd = JNativeUtils.findUniqueIEHwnd();
    if (hwnd == null) {
      System.out.println("没有找到IE窗体句柄");
    }
    JNativeUtils.user32.SetForegroundWindow(hwnd);
    Thread.sleep(1000);
    CommandSession session = new CommandSession();
    ClickCmd cmd = new ClickCmd(991, 355);
    cmd.setHwnd(hwnd);
    cmd.setRepeat(3);
    cmd.setExpire(1000);
    cmd.setRepeatWait(1);
    System.out.println(JSON.toJSONString(cmd));
    cmd.execute(session);

  }
}
