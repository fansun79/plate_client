package com.sun.plate.cmd.impl;

import com.alibaba.fastjson.JSON;
import com.sun.jna.platform.win32.WinDef;
import com.sun.plate.cmd.AbstractCommand;
import com.sun.plate.cmd.CommandSession;
import com.sun.plate.util.jna.JNativeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 输入事件
 * Created by sun on 2017/10/10.
 */
public class InputCmd extends AbstractCommand {

  private int x;

  private int y;

  private String valueName;

  public InputCmd() {
  }

  public InputCmd(int x, int y, String valueName) {
    this.x = x;
    this.y = y;
    this.valueName = valueName;
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

  public String getValueName() {
    return valueName;
  }

  public void setValueName(String valueName) {
    this.valueName = valueName;
  }

  @Override
  public boolean doExeute(CommandSession session) throws Exception {

//            logger.debug("触发输入事件,remark:{}",this.getRemark());
    String value = (String) session.get(this.valueName);
    if (value == null || StringUtils.isBlank(value)) {
      logger.error("输入参数不存在");
      this.info("输入参数不存在");
      return true;
    }
    this.info("输入参数：" + value);
    JNativeUtils.keyboardInput(this.getHwnd(), this.x, this.y, value);
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
    session.add("test", "99");
    InputCmd cmd = new InputCmd(1022, 425, "test");
    cmd.setHwnd(hwnd);
    cmd.setRepeat(3);
    cmd.setExpire(1000);
    cmd.setRepeatWait(1);
    System.out.println(JSON.toJSONString(cmd));
    cmd.execute(session);

  }
}
