package com.sun.plate.util.image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by sun on 2016/1/21.
 */
public class PixelMatrix {

  public final BufferedImage image;

  public PixelMatrix(BufferedImage image)
  {
    this.image = image;
  }

  public int getWidth()
  {
    return this.image.getWidth();
  }

  public int getHeight()
  {
    return this.image.getHeight();
  }

  public Pixel getPixel(int x ,int y)
  {
    if((x>=0 && x<image.getWidth())
        && (y>=0 && y<image.getHeight()) )
    {
      return new Pixel(x,y,new Color(image.getRGB(x,y)));
    }
    else
    {
      return null;
    }
  }

  public Pixel getUpperLeft(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return this.getPixel(x-1,y-1);
  }

  public Pixel getUpper(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return this.getPixel(x,y-1);
  }

  public Pixel getUpperRight(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return  this.getPixel(x+1,y-1);
  }

  public Pixel getLeft(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return this.getPixel(x-1,y);
  }

  public Pixel getRight(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return this.getPixel(x+1,y);
  }

  public Pixel getBottomLeft(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return this.getPixel(x-1,y+1);
  }

  public Pixel getBottom(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return this.getPixel(x,y+1);
  }

  public Pixel getBottomRight(Pixel pixel)
  {
    int x = pixel.x;
    int y = pixel.y;
    return this.getPixel(x+1,y+1);

  }

  /**
   *  以pixel为左上顶点，取宽为width,高为height的区域块
   * @param pixel
   * @param width
   * @param height
   * @return
   */
  public PixelMatrix getBulk(Pixel pixel,int width, int height){
    int x = pixel.x;
    int y = pixel.y;
//        int w = width;
//        int h = height;
    if(pixel.x+width>this.getWidth())
    {
      x =x-((pixel.x+width)-this.getWidth());
    }
    if(pixel.y+height>this.getHeight())
    {
      y = y-((pixel.y+height)-this.getHeight());
    }

    BufferedImage bulkImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    for(int i = 0 ; i<width;i++)
    {
      for(int k=0;k<height;k++)
      {
        bulkImage.setRGB(i,k,this.image.getRGB(x+i,y+k));
      }
    }
    return  new PixelMatrix(bulkImage);
  }

}
