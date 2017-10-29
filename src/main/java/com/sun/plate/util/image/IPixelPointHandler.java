package com.sun.plate.util.image;

import java.awt.*;

/**
 * Created by sun on 2016/1/18.
 */
public interface IPixelPointHandler {

  /**
   *  根据像素点的颜色处理
   * @param x
   * @param y
   * @param self
   * @param upperLeft
   * @param upper
   * @param upperRight
   * @param left
   * @param right
   * @param bottomLeft
   * @param bottom
   * @param bottomRight
   */
  public void handle(int x, int y,Color self,Color upperLeft,Color upper,Color upperRight,Color left, Color right,
      Color bottomLeft,Color bottom,Color bottomRight);

}
