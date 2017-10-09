package com.sun.plate.ocr.filter;

import java.awt.image.BufferedImage;

/**
 * 图片预处理器
 * Created by sun on 2017/10/6.
 */
public interface ImagePreprocessingFilter {

  /**
   * 图片预处理，如二值化，去噪点，去干扰线，根据不同情况处理
   * @param image
   * @return
   */
  public BufferedImage doProcess(BufferedImage image);

}
