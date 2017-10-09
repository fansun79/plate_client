package com.sun.plate.ocr;

import com.sun.plate.ocr.filter.ImagePreprocessingFilter;
import java.awt.image.BufferedImage;

/**
 * 图像识别器
 * Created by sun on 2017/10/6.
 */
public interface ImageRecognizer {

  /**
   *  添加图片预处理器
   * @param key
   * @param filter
   */
  public  void addImagePreprocessingFilter(String key, ImagePreprocessingFilter filter);

  /**
   * 按类型识别图像
   * @param image
   * @param key
   * @return
   */
  String recognizeImage(BufferedImage image,String key);

  String recognizeImage(BufferedImage image,String key,Integer n);
}
