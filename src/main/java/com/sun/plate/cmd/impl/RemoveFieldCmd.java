package com.sun.plate.cmd.impl;

import com.alibaba.fastjson.JSON;
import com.sun.jna.platform.win32.WinDef;
import com.sun.plate.cmd.AbstractCommand;
import com.sun.plate.cmd.CommandSession;
import com.sun.plate.entity.xnative.XPoint;
import com.sun.plate.util.jna.JNativeUtils;

/**
 * Created by sun on 2017/10/11.
 */
public class RemoveFieldCmd extends AbstractCommand {

  private XPoint fieldPoint ;

  private int wait = 10;

  private int deleteRepeat = 1;

  public RemoveFieldCmd(){}
  public RemoveFieldCmd(XPoint fieldPoint)
  {
    this.fieldPoint = fieldPoint;
  }

  public XPoint getFieldPoint() {
    return fieldPoint;
  }

  public void setFieldPoint(XPoint fieldPoint) {
    this.fieldPoint = fieldPoint;
  }

  public int getWait() {
    return wait;
  }

  public void setWait(int wait) {
    this.wait = wait;
  }

  public int getDeleteRepeat() {
    return deleteRepeat;
  }

  public void setDeleteRepeat(int deleteRepeat) {
    this.deleteRepeat = deleteRepeat;
  }

  @Override
  public boolean doExeute(CommandSession session) throws Exception {
    JNativeUtils.keyboardCtrlA(this.getHwnd(),fieldPoint.x,fieldPoint.y,this.wait,this.deleteRepeat);
    return false;
  }

  public static  final void main(String[] args)throws Exception
  {
    WinDef.HWND hwnd = JNativeUtils.findUniqueIEHwnd();
    if(hwnd ==null)
    {
      System.out.println("没有找到IE窗体句柄");
    }
    JNativeUtils.user32.SetForegroundWindow(hwnd);
    Thread.sleep(1000);
    CommandSession session = new CommandSession();
    RemoveFieldCmd cmd = new RemoveFieldCmd(new XPoint(1022,425));
    cmd.setHwnd(hwnd);
    cmd.setRepeat(1);
    cmd.setExpire(0);
    cmd.setRepeatWait(1);
    System.out.println(JSON.toJSONString(cmd));
    cmd.execute(session);
  }
}
