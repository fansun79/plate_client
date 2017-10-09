package com.sun.plate.util.jna;


import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * Created by sun on 2015/11/17.
 */
public final class JNativeUtils {
  private static Logger logger = LoggerFactory.getLogger(JNativeUtils.class);
  public static User32Ext USER32EXT  = (User32Ext) Native.loadLibrary("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
  public static User32 user32 = User32.INSTANCE;

  public static void mouseClick(WinDef.HWND hwnd, int x, int y, boolean isTest) {
    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd,rect);
    user32.SetForegroundWindow(hwnd);
    LPoint p  = new LPoint();
    boolean  r = USER32EXT.GetCursorPos(p);
    r = USER32EXT.SetCursorPos(rect.left+x,rect.top+y);
    if(!isTest)
    {
      USER32EXT.mouse_event(0x0002 | 0x0004,0,0,0,0); //点击button
    }
  }

  public static WinDef.HWND findUniqueIEHwnd()
  {
    WinDef.HWND hwnd = USER32EXT.FindWindowEx(null,null,"IEFrame",null);
    if(hwnd == null)
    {
      logger.debug("没有找到IEFrame窗体");
      return null;
    }
    hwnd = USER32EXT.FindWindowEx(hwnd,null,"Frame Tab",null);
    if(hwnd == null)
    {
      logger.debug("没有找到Frame Tab窗体");
      return null;
    }
    hwnd  = USER32EXT.FindWindowEx(hwnd,null,"TabWindowClass",null);
    if(hwnd == null)
    {
      logger.debug("没有找到TabWindowClass窗体");
      return null;
    }
    hwnd = USER32EXT.FindWindowEx(hwnd,null,"Shell DocObject View",null);
    if(hwnd == null)
    {
      logger.debug("没有找到Shell DocObject View窗体");
      return null;
    }
    hwnd = USER32EXT.FindWindowEx(hwnd,null,"Internet Explorer_Server",null);
    if(hwnd == null)
    {
      logger.debug("没有找到Internet Explorer_Server窗体");
      return null;
    }
    return hwnd;
  }

  public static BufferedImage screenshot(WinDef.HWND hwnd, int x, int y,int width, int height ) throws Exception{

    Date d1 = new Date();
    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd,rect);
//        user32.SetForegroundWindow(hwnd);
    x = rect.left+x;
    y =  rect.top+y;
    Rectangle screenRectangle = new Rectangle(x,y,width,height );
    Robot robot = new Robot();
    BufferedImage image = robot.createScreenCapture(screenRectangle);
    Date d2= new Date();
//        logger.debug("截图耗时{}毫秒",d2.getTime()-d1.getTime());
    return image;
  }

  public static void keyboardInput(WinDef.HWND hwnd, int x, int y, String value)
  {
    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd,rect);

    user32.SetForegroundWindow(hwnd);
    LPoint p  = new LPoint();
    boolean  r = USER32EXT.GetCursorPos(p);
    r = USER32EXT.SetCursorPos(rect.left+x, rect.top+y);

    USER32EXT.mouse_event(0x0002 | 0x0004,0,0,0,0); //点击输入框
    //输入退格键
//        for(int i = 0; i<20; i++)
//        {
//            USER32EXT.keybd_event('\b','\0',0,0);
//            USER32EXT.keybd_event('\b','\0',2,0);
//            Thread.sleep(5);
//        }
//        Thread.sleep(10);
    char[] chars =  value.toCharArray();
    for(char c : chars)
    {
      USER32EXT.keybd_event(c,'\0',0,0);
      USER32EXT.keybd_event(c,'\0',2,0);
    }
  }

  public static void keyboardCtrlA(WinDef.HWND hwnd, int x, int y,int wait,int repeat)
  {
    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd,rect);

    user32.SetForegroundWindow(hwnd);
    LPoint p  = new LPoint();
    boolean  r = USER32EXT.GetCursorPos(p);
    r = USER32EXT.SetCursorPos(rect.left+x, rect.top+y);

    USER32EXT.mouse_event(0x0002 | 0x0004,0,0,0,0); //点击输入框
    USER32EXT.keybd_event((char)0x11, '\0', 0, 0);
    USER32EXT.keybd_event('a', '\0', 0, 0);
    USER32EXT.keybd_event('a', '\0', 0x0002, 0);
    USER32EXT.keybd_event((char)0x11, '\0', 0x0002,0);
    if(wait >0)
    {
      try
      {
        Thread.sleep(wait);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    int i = 0;
    do {
      USER32EXT.keybd_event((char)0x2E, '\0', 0, 0);
      USER32EXT.keybd_event((char)0x2E , '\0',2,0);
      i++;
    }
    while (i<repeat);

  }


}
