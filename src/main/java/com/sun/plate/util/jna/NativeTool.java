package com.sun.plate.util.jna;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sun on 2015/6/26.
 */
public class NativeTool {

  private static final Logger logger = LoggerFactory.getLogger(NativeTool.class);
  public static User32Ext USER32EXT = (User32Ext) Native
      .loadLibrary("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
  public static User32 user32 = User32.INSTANCE;

  public static void click(int win_point, int x, int y) {

    Pointer pointer = new Pointer(win_point);
    WinDef.HWND hwnd = new WinDef.HWND(pointer);
    click(hwnd, x, y);
  }

  public static void click(WinDef.HWND hwnd, int x, int y) {
    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd, rect);
    user32.SetForegroundWindow(hwnd);
    LPoint p = new LPoint();
    boolean r = USER32EXT.GetCursorPos(p);
    r = USER32EXT.SetCursorPos(rect.left + x, rect.top + y);
    USER32EXT.mouse_event(0x0002 | 0x0004, 0, 0, 0, 0); //点击button
//        USER32EXT.SetCursorPos(p.x,p.y);
  }

  public static void clickTest(int win_point, int x, int y) {

    Pointer pointer = new Pointer(win_point);
    WinDef.HWND hwnd = new WinDef.HWND(pointer);
    clickTest(hwnd, x, y);
  }

  public static void clickTest(WinDef.HWND hwnd, int x, int y) {
    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd, rect);
    user32.SetForegroundWindow(hwnd);
    LPoint p = new LPoint();
    boolean r = USER32EXT.GetCursorPos(p);
    r = USER32EXT.SetCursorPos(rect.left + x, rect.top + y);
  }

  public static void input(int win_point, int x, int y, String content) throws Exception {

    Pointer pointer = new Pointer(win_point);
    WinDef.HWND hwnd = new WinDef.HWND(pointer);
    input(hwnd, x, y, content);

  }

  public static void input(WinDef.HWND hwnd, int x, int y, String content) throws Exception {

    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd, rect);

    user32.SetForegroundWindow(hwnd);
    LPoint p = new LPoint();
    boolean r = USER32EXT.GetCursorPos(p);
    r = USER32EXT.SetCursorPos(rect.left + x, rect.top + y);

    USER32EXT.mouse_event(0x0002 | 0x0004, 0, 0, 0, 0); //点击输入框
    //输入退格键
//        for(int i = 0; i<20; i++)
//        {
//            USER32EXT.keybd_event('\b','\0',0,0);
//            USER32EXT.keybd_event('\b','\0',2,0);
//            Thread.sleep(5);
//        }
//        Thread.sleep(10);
    char[] chars = content.toCharArray();
    for (char c : chars) {
      USER32EXT.keybd_event(c, '\0', 0, 0);
      USER32EXT.keybd_event(c, '\0', 2, 0);
    }

  }

  public static BufferedImage capture(int win_point, int left_offset, int top_offset, int width,
      int height) throws Exception {
    Pointer pointer = new Pointer(win_point);
    WinDef.HWND hwnd = new WinDef.HWND(pointer);
    return capture(hwnd, left_offset, top_offset, width, height);

  }

  public static BufferedImage capture(WinDef.HWND hwnd, int left_offset, int top_offset, int width,
      int height) throws Exception {
    LRect rect = new LRect();
    USER32EXT.GetWindowRect(hwnd, rect);
//        user32.SetForegroundWindow(hwnd);
    int x = rect.left + left_offset;
    int y = rect.top + top_offset;

    return capture(x, y, width, height);

  }

  public static BufferedImage capture(int x, int y, int width, int height) throws Exception {
//        Date d1 = new Date();
    Rectangle screenRectangle = new Rectangle(x, y, width, height);
    Robot robot = new Robot();
    BufferedImage image = robot.createScreenCapture(screenRectangle);
//        Date d2= new Date();
//        logger.debug("截图耗时{}毫秒",d2.getTime()-d1.getTime());
    return image;
  }


  public static WinDef.HWND findIEHwnd() {
    WinDef.HWND hwnd = USER32EXT.FindWindowEx(null, null, "IEFrame", null);
    if (hwnd == null) {
      logger.debug("没有找到IEFrame窗体");
      return null;
    }
    hwnd = USER32EXT.FindWindowEx(hwnd, null, "Frame Tab", null);
    if (hwnd == null) {
      logger.debug("没有找到Frame Tab窗体");
      return null;
    }
    hwnd = USER32EXT.FindWindowEx(hwnd, null, "TabWindowClass", null);
    if (hwnd == null) {
      logger.debug("没有找到TabWindowClass窗体");
      return null;
    }
    hwnd = USER32EXT.FindWindowEx(hwnd, null, "Shell DocObject View", null);
    if (hwnd == null) {
      logger.debug("没有找到Shell DocObject View窗体");
      return null;
    }
    hwnd = USER32EXT.FindWindowEx(hwnd, null, "Internet Explorer_Server", null);
    if (hwnd == null) {
      logger.debug("没有找到Internet Explorer_Server窗体");
      return null;
    }
    return hwnd;
  }


  public static final void main(String[] args) throws Exception {
//          WinDef.HWND  IEHWND = null;
//
//        user32.EnumWindows(new WinUser.WNDENUMPROC() {
//            int count;
//
//            public boolean callback(WinDef.HWND wnd, Pointer data) {
//                System.out.println("Found window " + wnd + ", total " + ++count);
//                int buflen = 150;
//                char[] lpString = new char[300];
////                user32.GetWindowText(wnd, lpString, buflen);
//                user32.GetClassName(wnd,lpString,buflen);
//                if("IEFrame".equals(lpString))
//                {
//                     long h =  wnd.getPointer().getLong(0);
//                    System.out.println(h);
//                    return false;
//                }
//                return true;
//            }
//        }, null);
    WinDef.HWND IE_HWND = findIEHwnd();
    WinDef.HWND flash_hwnd = USER32EXT
        .FindWindowEx(IE_HWND, null, "MacromediaFlashPlayerActiveX", null);
    user32.hashCode();
    if (IE_HWND == null) {
      System.out.println("没找到");
    } else {

      System.out.println(Pointer.nativeValue(IE_HWND.getPointer()));
      int x = 331;
      int y = 113;
      capture(IE_HWND, x, y, 100, 100);

      for (int i = 0; i < 20; i++) {
        capture(IE_HWND, x, y, 100, 100);
        click(IE_HWND, x, y);
      }

    }
  }
}



