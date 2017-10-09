package com.sun.plate.ocr;


import com.sun.plate.ocr.filter.ImagePreprocessingFilter;
import com.sun.plate.util.PlateUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by sun on 2015/7/7.
 */

public abstract class AbstractImageRecognizer implements ImageRecognizer {

  private static Logger logger = LoggerFactory.getLogger(AbstractImageRecognizer.class);


  protected Map<String, List<TradingImage>> tradingImageMap = TradingImage.loadAll();

  protected  Map<String,ImagePreprocessingFilter> imagePreprocessingFilterMap = new HashMap<String,ImagePreprocessingFilter>();


  public static boolean isDebug = false;

  @Value("ocr.tempdir")
  private  String tempDir;

  public AbstractImageRecognizer() {
  }


  public  void addImagePreprocessingFilter(String key, ImagePreprocessingFilter filter)
  {
    if(filter == null)
    {
      return;
    }
    this.imagePreprocessingFilterMap.put(key,filter);
  }

  public  ImagePreprocessingFilter getImagePreprocessingFilter(String key)
  {
    return this.imagePreprocessingFilterMap.get(key);
  }

  @Override
  public String recognizeImage(BufferedImage image,String key)
  {
    return  this.recognizeImage(image,key,null);
  }


  protected String getTempDir() {
    return this.tempDir;
  }

  public boolean isDebug() {
    return isDebug;
  }

  public void setIsDebug(boolean isDebug) {
    this.isDebug = isDebug;
  }

  protected void writeDebugImage(BufferedImage image, String remark) {
    if (!this.isDebug) {
      return;
    }
    try {
      String fileName = DateFormatUtils.format(new Date(),"yyyyMMddmmHHssSSS") + ".jpg";
      File file = new File(this.getTempDir() + "\\" + fileName);
      ImageIO.write(image, "jpg", file);
      logger.debug("写入{},说明：{}", fileName, remark);
    } catch (Exception e) {
      logger.error("保存临时图像文件错误", e);
    }

  }

  public BufferedImage cleanImage(BufferedImage bufferedImage)
//            throws IOException
  {

    int h = bufferedImage.getHeight();
    int w = bufferedImage.getWidth();

    // 灰度化
    int[][] gray = new int[w][h];
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int argb = bufferedImage.getRGB(x, y);
        // 图像加亮（调整亮度识别率非常高）
        int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
        int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
        int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
        if (r >= 255) {
          r = 255;
        }
        if (g >= 255) {
          g = 255;
        }
        if (b >= 255) {
          b = 255;
        }
        gray[x][y] = (int) Math
            .pow((Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2)
                * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
      }
    }

    // 二值化
    int threshold = ostu(gray, w, h);
    BufferedImage binaryBufferedImage = new BufferedImage(w, h,
        BufferedImage.TYPE_BYTE_BINARY);
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        if (gray[x][y] > threshold) {
          gray[x][y] |= 0x00FFFF;
        } else {
          gray[x][y] &= 0xFF0000;
        }
        binaryBufferedImage.setRGB(x, y, gray[x][y]);
      }
    }
    return binaryBufferedImage;
  }


  public int ostu(int[][] gray, int w, int h) {
    int[] histData = new int[w * h];
    // Calculate histogram
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int red = 0xFF & gray[x][y];
        histData[red]++;
      }
    }

    // Total number of pixels
    int total = w * h;

    float sum = 0;
    for (int t = 0; t < 256; t++)
      sum += t * histData[t];

    float sumB = 0;
    int wB = 0;
    int wF = 0;

    float varMax = 0;
    int threshold = 0;

    for (int t = 0; t < 256; t++) {
      wB += histData[t]; // Weight Background
      if (wB == 0)
        continue;

      wF = total - wB; // Weight Foreground
      if (wF == 0)
        break;

      sumB += (float) (t * histData[t]);

      float mB = sumB / wB; // Mean Background
      float mF = (sum - sumB) / wF; // Mean Foreground

      // Calculate Between Class Variance
      float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

      // Check if new maximum found
      if (varBetween > varMax) {
        varMax = varBetween;
        threshold = t;
      }
    }

    return threshold;
  }

  public static BufferedImage removeCaptchaBackgroud(BufferedImage img, int whiteValue, int whiteLimit) {
    int width = img.getWidth();
    int height = img.getHeight();
//        System.out.println(img.getMinX());
//        System.out.println(img.getMinY());
//        System.out.println(width);
//        System.out.println(height);
    for (int x = 0; x < width; x++) {
//            try {
      for (int y = 0; y < height; y++) {
        int sum = 0;

        if ((y - 1 >= 0) && isWhite(img.getRGB(x, y - 1), whiteValue)) {
          sum++;
        }
        if ((y + 1 < height) && isWhite(img.getRGB(x, y + 1), whiteValue)) {
          sum++;
        }
        if ((x - 1 >= 0) && isWhite(img.getRGB(x - 1, y), whiteValue)) {
          sum++;
        }
        if ((x + 1 < width) && isWhite(img.getRGB(x + 1, y), whiteValue)) {
          sum++;
        }

        if ((x - 1 >= 0 && y - 1 >= 0) && isWhite(img.getRGB(x - 1, y - 1), whiteValue)) {
          sum++;
        }
        if ((x + 1 < width && y - 1 >= 0) && isWhite(img.getRGB(x + 1, y - 1), whiteValue)) {
          sum++;
        }
        if ((x + 1 < width && y + 1 < height) && isWhite(img.getRGB(x + 1, y + 1), whiteValue)) {
          sum++;
        }
        if ((x - 1 >= 0 && y + 1 < height) && isWhite(img.getRGB(x - 1, y + 1), whiteValue)) {
          sum++;
        }


        if ((x == 0 || x == width - 1) && sum > whiteLimit - 3) {
          img.setRGB(x, y, Color.WHITE.getRGB());
        } else if ((y == 0 || y == height - 1) && sum > whiteLimit - 3) {
          img.setRGB(x, y, Color.WHITE.getRGB());
        } else if (sum > whiteLimit) {
//                        System.out.println("去除噪点");
          img.setRGB(x, y, Color.WHITE.getRGB());
        }
      }
//            }
//            catch (Exception e)
//            {
//                continue;
//            }
    }
    return img;
  }

  public static boolean isBlack(int colorInt, int whiteValue) {
    Color color = new Color(colorInt);
    if (color.getRed() + color.getGreen() + color.getBlue() <= whiteValue) {
      return true;
    }
    return false;
  }

  public static boolean isWhite(int colorInt, int whiteValue) {
    Color color = new Color(colorInt);
    if (color.getRed() + color.getGreen() + color.getBlue() > whiteValue) {
      return true;
    }
    return false;
  }

  protected abstract String getTradingDataDirName();
}

