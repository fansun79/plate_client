package com.sun.plate.config;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.plate.entity.xnative.NativeInfo;
import com.sun.plate.util.jna.JNativeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sun on 2017/10/6.
 */
@Configuration
public class PlateConfig {

  private static final Logger logger = LoggerFactory.getLogger(PlateConfig.class);

  @Value("${plate.client.hwnd.point:-1}")
  private long hwndPoint;

  @Value("${plate.client.id}")
  private String clientId;


  /**
   * 获取默认的IE窗口句柄
   * @return
   */
  @Bean
  public NativeInfo nativeInfo()
  {
    NativeInfo localInfo = new NativeInfo();

    WinDef.HWND hwnd = null;
    if (hwndPoint <= 0) {
      hwnd = JNativeUtils.findUniqueIEHwnd();
    } else {
      Pointer pointer = new Pointer(hwndPoint);
      hwnd = new WinDef.HWND(pointer);
    }
    if (hwnd == null) {
      throw new java.lang.IllegalArgumentException("默认桌面上没有找到IE窗体");
    }
    localInfo.setIeFramehwnd(hwnd);
    localInfo.setClientId(this.clientId);
    return localInfo;
  }

}
