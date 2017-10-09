package com.sun.plate.entity.xnative;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * 本地信息
 * Created by sun on 2017/10/6.
 */
public class NativeInfo {

  /**
   * 当前IE界面的Hwnd
   */
  private WinDef.HWND ieFramehwnd;
  /**
   * 客户端唯一标识
   */
  private String clientId;


  public HWND getIeFramehwnd() {
    return ieFramehwnd;
  }

  public void setIeFramehwnd(HWND ieFramehwnd) {
    this.ieFramehwnd = ieFramehwnd;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }
}
