package com.sun.plate.util.image;

/**
 * Created by sun on 2017/10/19.
 */
import com.alibaba.fastjson.JSON;

import java.awt.*;

/**
 *  像素点
 * Created by sun on 2016/1/21.
 */
public class Pixel {
  /**
   *  x坐标
   */
  public  final int x;
  /**
   *  y坐标
   */
  public  final int y;
  /**
   *  ARGB
   */
  public final Color rgb;

  /**
   *  HSL
   */
  public final HSL hsl;

  /**
   *  HSV
   */
  public  final HSV hsv;

  /**
   *  灰度值
   */
  public final int gray;


  public Pixel(int x,int y,Color color)
  {
    this.x = x;
    this.y = y;
    this.rgb = color;
    this.hsl  = HSL.RGBtoHSL(color.getRed(),color.getGreen(),color.getBlue());
    this.hsv = new HSV(color);
    this.gray = ImageUtil.getGray(color);

  }

  /**
   *  计算两个像素点之间的色差距离
   * @param pixel
   * @return
   */
  public double compareToRGB(Pixel pixel)
  {
    return ImageUtil.colorValueComp(this.rgb,pixel.rgb);
  }

  public double compareToRGBPercent(Pixel pixel)
  {
    return ImageUtil.colorPercentComp(this.rgb, pixel.rgb);
  }

  /**
   *  是否是个彩色点
   * @return
   */
  public boolean isColorPoint()
  {
    return   !(hsv.S<20 &&hsv.V>90);
  }

  @Override
  public String toString()
  {
    return JSON.toJSONString(this);
  }
}
