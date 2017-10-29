package com.sun.plate.cmd;

import com.sun.jna.platform.win32.WinDef;

/**
 * Created by sun on 2017/10/10.
 */
public interface Command {

  /**
   * 执行前等待毫秒数
   */
  int getBeforeWait();

  /**
   * 执行后等待毫秒数
   */
   int getAfterWait();

  /**
   * 每次重复调用中间的休眠时间
   */
   int getRepeatWait();

  /**
   * 重复次数
   */
   int getRepeat();

  /**
   * 过期持续时间，单位毫秒
   */
   int getExpire();

  /**
   * 窗体句柄
   */
   WinDef.HWND getHwnd();

  /**
   * 备注
   */
   String getRemark();

  /**
   * 记录日志
   */
   void info(String msg);

  /**
   * 警告日志
   */
   void warn(String msg, Throwable t);

  /**
   * 执行命令
   *
   * @param session 会话参数
   */
   void execute(CommandSession session);
}
