package com.sun.plate.util.image;

import com.sun.plate.entity.xnative.XPoint;
import com.sun.plate.util.Pair;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sun on 2017/10/18.
 */
public class ImageUtil {

  private final static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
  /**
   *  RGB最大色差
   */
  private static double  MAX_RGB_COMP  = Math.sqrt(255*255+255*255+255*255);

  private static int MAX_GRAY = ImageUtil.getGray(Color.WHITE);

  //按空白分割分割图片
  public static List<BufferedImage> SplitImage(BufferedImage img)
  {
    List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
    int width = img.getWidth();
    int height = img.getHeight();
    List<Integer> weightlist = new ArrayList<Integer>();
    int lastX = -1;
    boolean colHasBlack = false;
    int maxY = -1;
    int minY= -1;
    for(int x =0 ; x<width;x++)
    {
      for(int y = 0 ; y<height;y++)
      {
        Color color =  new Color(img.getRGB(x,y));
        if(color.equals(Color.BLACK))
        {
          if(minY <0 && maxY<0)
          {
            maxY =y;
            minY=y;
          }
          else
          {
            if(y>maxY )
            {
              maxY  = y;
            }
            if(y<minY)
            {
              minY = y;
            }
          }

          colHasBlack = true;
        }

      }

//            if(!colHasBlack)
//            {
//                System.out.println(x+"列纯白,lastX"+lastX);
//            }
//            else
//            {
//                System.out.println(x+"列有黑,lastX"+lastX);
//            }

      if(colHasBlack && lastX<0)
      {
        lastX = x;
      }

      if(!colHasBlack && lastX >=0)
      {
//                System.out.println(lastX+"-"+x+"-"+minY+"-"+maxY+"列sub");
        int sub_x =  lastX>width?width:lastX;
        int sub_y =  minY>height?height:minY;
        int sub_w = (x - lastX)>width?width:(x - lastX);
        int sub_h =  (maxY-minY+1)>height?height:(maxY-minY+1);
        if(sub_x >= 0 && sub_y>=0 && sub_w>=0 && sub_h>=0)
        {
          subImgs.add(img.getSubimage(lastX, minY, x - lastX, maxY-minY+1));
        }

        lastX = -1;
        maxY = -1;
        minY= -1;
      }
      if( (x ==width-1) && lastX>=0)
      {
//                System.out.println(lastX+"-"+x+"-"+minY+"-"+maxY+"列sub");
        int sub_x =  lastX>width?width:lastX;
        int sub_y =  minY>height?height:minY;
        int sub_w = (x - lastX+1)>width?width:(x - lastX+1);
        int sub_h =  (maxY-minY+1)>height?height:(maxY-minY+1);
        if(sub_x >= 0 && sub_y>=0 && sub_w>=0 && sub_h>=0)
        {
          subImgs.add(img.getSubimage(lastX, minY, x - lastX+1, maxY-minY+1));
        }
        lastX = -1;
        maxY = -1;
        minY= -1;
      }
      colHasBlack = false;

    }
    return subImgs;
  }

  /**
   *  统计一幅图的HSL色彩分布
   * @param image
   * @return
   */
  public static Map<HSL,Integer> countHSL(BufferedImage image)
  {
    Map<HSL,Integer> counts = new HashMap<HSL,Integer>();
    int width = image.getWidth();
    int height = image.getHeight();
    for(int i = 0;i<width;i++ )
    {
      for(int j =0 ; j<height; j++)
      {
        int argb = image.getRGB(i, j);
        Color color = new Color(argb);
        HSL hsl = HSL.RGBtoHSL(color.getRed(), color.getGreen(), color.getBlue());
        Integer count = null;
        if(counts.containsKey(hsl))
        {
          count = counts.get(hsl);
        }
        else
        {
          count = 0;
        }
        count++;
        counts.put(hsl,count);
      }

    }
    return counts;
  }

  /**
   *  将HSL按L值累加排序，按颜色最亮的颜色在前
   * @param image
   * @return 返回H值的队列
   */
  public static  List<Pair<Integer,Integer>> sortByluminance(BufferedImage image )
  {
    Map<Integer,Integer> counts = new HashMap<Integer,Integer>();
    int width = image.getWidth();
    int height = image.getHeight();
    for(int i = 0;i<width;i++ )
    {
      for(int j =0 ; j<height; j++)
      {
        int argb = image.getRGB(i, j);
        Color color = new Color(argb);
        HSL hsl = HSL.RGBtoHSL(color.getRed(), color.getGreen(), color.getBlue());
        Integer count = null;
        if(counts.containsKey(hsl.getH()))
        {
          count = counts.get(hsl.getH());
        }
        else
        {
          count = 0;
        }
        count+=hsl.getL();
        counts.put(hsl.getH(),count);
      }

    }

    List<Map.Entry<Integer,Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(counts.entrySet());
    Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
      @Override
      public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
        return o2.getValue()-o1.getValue();
      }
    });

    List<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
    for(Map.Entry<Integer,Integer> entry : list)
    {
      pairs.add(Pair.intPair(entry.getKey(),entry.getValue()));
    }

    return pairs;
  }

  /**
   *  将图像中和source相同或不相同的颜色，替换成target描述的颜色
   * @param image
   * @param image
   * @param target
   * @param eq
   */
  public static void replaceColor(BufferedImage image,Integer h,Color target,boolean  eq)
  {
    int width = image.getWidth();
    int height = image.getHeight();
    for(int x= 0;x<width;x++ )
    {
      for(int y=0 ; y<height; y++)
      {
        int argb = image.getRGB(x,y);
        Color color = new Color(argb);
        HSL hsl = HSL.RGBtoHSL(color.getRed(),color.getGreen(),color.getBlue());
        if((eq && hsl.getH() == h)
            || (!eq && hsl.getH()!=h))
        {
          image.setRGB(x,y,target.getRGB());
        }
      }
    }
  }


  public BufferedImage thresholdBinary(BufferedImage src, BufferedImage dest) {
    int width = src.getWidth();
    int height = src.getHeight();
    if ( dest == null )
      dest = createCompatibleDestImage( src, null );

    int[] inPixels = new int[width*height];
    int[] outPixels = new int[width*height];

    getRGB(src, 0, 0, width, height, inPixels);
    int index = 0;
    int means = getThreshold(inPixels, height, width);
    for(int row=0; row<height; row++) {
      int ta = 0, tr = 0, tg = 0, tb = 0;
      for(int col=0; col<width; col++) {
        index = row * width + col;
        ta = (inPixels[index] >> 24) & 0xff;
        tr = (inPixels[index] >> 16) & 0xff;
        tg = (inPixels[index] >> 8) & 0xff;
        tb = inPixels[index] & 0xff;
        if(tr > means) {
          tr = tg = tb = 255; //white
        } else {
          tr = tg = tb = 0; // black
        }
        outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
      }
    }
    setRGB( dest, 0, 0, width, height, outPixels );
    return dest;
  }

  public static int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
    int type = image.getType();
    if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
      return (int [])image.getRaster().getDataElements( x, y, width, height, pixels );
    return image.getRGB( x, y, width, height, pixels, 0, width );
  }

  public static void setRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
    int type = image.getType();
    if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
      image.getRaster().setDataElements( x, y, width, height, pixels );
    else
      image.setRGB( x, y, width, height, pixels, 0, width );
  }


  private int getThreshold(int[] inPixels, int height, int width) {
    // maybe this value can reduce the calculation consume;
    int inithreshold = 127;
    int finalthreshold = 0;
    int temp[] = new int[inPixels.length];
    for(int index=0; index<inPixels.length; index++) {
      temp[index] = (inPixels[index] >> 16) & 0xff;
    }
    List<Integer> sub1 = new ArrayList<Integer>();
    List<Integer> sub2 = new ArrayList<Integer>();
    int means1 = 0, means2 = 0;
    while(finalthreshold != inithreshold) {
      finalthreshold = inithreshold;
      for(int i=0; i<temp.length; i++) {
        if(temp[i] <= inithreshold) {
          sub1.add(temp[i]);
        } else {
          sub2.add(temp[i]);
        }
      }
      means1 = getMeans(sub1);
      means2 = getMeans(sub2);
      sub1.clear();
      sub2.clear();
      inithreshold = (means1 + means2) / 2;
    }
    long start = System.currentTimeMillis();
    System.out.println("Final threshold  = " + finalthreshold);
    long endTime = System.currentTimeMillis() - start;
    System.out.println("Time consumes : " + endTime);
    return finalthreshold;
  }

  public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
    if ( dstCM == null )
      dstCM = src.getColorModel();
    return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
  }

  private static int getMeans(List<Integer> data) {
    int result = 0;
    int size = data.size();
    for(Integer i : data) {
      result += i;
    }
    return (result/size);
  }

  /**
   *  图片灰度化
   * @param image
   * @return
   */
  public static BufferedImage toGray(BufferedImage image)
  {
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    for(int i= 0 ; i < width ; i++){
      for(int j = 0 ; j < height; j++){
        int rgb = image.getRGB(i, j);
        grayImage.setRGB(i, j, rgb);
      }
    }

    return grayImage;
  }

  /**
   *  图片二值化
   * @param image
   * @return
   */
  public static BufferedImage toBinary(BufferedImage image,final int threshold)
  {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    handleImage(image, new IPixelPointHandler() {
      @Override
      public void handle(int x, int y, Color self, Color upperLeft, Color upper, Color upperRight, Color left, Color right, Color bottomLeft, Color bottom, Color bottomRight) {
        HSL hsl = HSL.RGBtoHSL(self.getRed(), self.getGreen(), self.getBlue());
        if (hsl.getL() < threshold) {
          newImage.setRGB(x, y, Color.BLACK.getRGB());
        } else {
          newImage.setRGB(x, y, Color.WHITE.getRGB());
        }
      }
    });
    return newImage;
  }

  /**
   *  计算两个像素点RGB颜色差距
   * @param color1
   * @param color2
   * @return
   */
  public static double colorValueComp(Color color1,Color color2)
  {
    if(color1 == null || color2 == null)
    {
      return 0;
    }
    int r1 = color1.getRed();
    int r2 = color2.getRed();
    int g1 = color1.getGreen();
    int g2 = color2.getGreen();
    int b1 = color1.getBlue();
    int b2 = color2.getBlue();
    double comp =Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
    return comp;
  }

  public static double  colorPercentComp(Color color1,Color color2)
  {
    double p = (double)colorValueComp(color1,color2)/MAX_RGB_COMP;
    logger.debug("p:{}", p);
    return p;
  }

  /**
   *  计算颜色的灰度值
   * @param color
   * @return
   */
  public static int getGray(Color color)
  {
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();
    int gray= (r*19595 + g*38469 + b*7472) >> 16;
    return gray;
  }



  private static void proccessImage(BufferedImage image,IPixelMatrixHandler handler)
  {
    PixelMatrix matrix = new PixelMatrix(image);
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    for(int x= 0;x<width;x++ ) {
      for (int y = 0; y < height; y++) {
        Pixel pixel = matrix.getPixel(x,y);
        handler.handle(pixel, matrix);
      }
    }
  }

  /**
   *  遍历图片中中的所有像素点
   * @param image
   * @param handler
   */
  private  static void handleImage(BufferedImage image,IPixelPointHandler handler)
  {
    int width = image.getWidth();
    int height = image.getHeight();
    for(int x= 0;x<width;x++ ) {
      for (int y = 0; y < height; y++) {

        Color self = new Color(image.getRGB(x,y));
        Color upperLeft = (x-1)>=0&&(y-1)>=0?new Color(image.getRGB(x-1,y-1)):null;
        Color upper = (y-1)>=0?new Color(image.getRGB(x,y-1)):null;
        Color upperRight = (x+1)<width&&(y-1)>=0?new Color(image.getRGB(x+1,y-1)):null;
        Color left = (x-1)>=0?new Color(image.getRGB(x-1,y)):null;
        Color right = (x+1)<width?new Color(image.getRGB(x+1,y)):null;
        Color bottomLeft =(x-1)>=0&&(y+1)<height?new Color(image.getRGB(x-1,y+1)):null;
        Color bottom = (y+1)<height?new Color(image.getRGB(x,y+1)):null;
        Color bottomRight = (x+1)<width&&(y+1)<height?new Color(image.getRGB(x+1,y+1)):null;
        handler.handle(x,y,self,upperLeft,upper,upperRight,left,right,bottomLeft,bottom,bottomRight);
      }
    }
  }


  /**
   *  统计图片中的RGB颜色排名
   * @param image
   * @return
   */
  public static List<Color> rankingRGB(BufferedImage image)
  {
    final Map<Integer,Integer> map = new HashMap<Integer,Integer>();
    handleImage(image, new IPixelPointHandler() {
      @Override
      public void handle(int x, int y, Color self, Color upperLeft, Color upper, Color upperRight, Color left,
          Color right, Color bottomLeft, Color bottom, Color bottomRight) {

        int rgb = self.getRGB();
        Integer sum = 0;
        if (map.containsKey(rgb)) {
          sum = map.get(rgb);
        }
        sum++;
        map.put(rgb, sum);
      }
    });
    List<Map.Entry<Integer,Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
      @Override
      public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
        return o2.getValue() - o1.getValue();
      }
    });
    List<Color> colors = new ArrayList<Color>(list.size());
    for(Map.Entry<Integer,Integer> e : list)
    {
      int rgb = e.getKey();
      Color color = new Color(rgb);
      colors.add(color);
    }
    return colors;
  }

  /**
   *   从图片中将亮度高于阀值的像素点涂白
   * @param threshold
   * @return
   */
  public static BufferedImage removePointByLightness(BufferedImage image, final int threshold)
  {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    handleImage(image, new IPixelPointHandler() {
      @Override
      public void handle(int x, int y, Color self, Color upperLeft, Color upper, Color upperRight, Color left, Color right, Color bottomLeft, Color bottom, Color bottomRight) {
        HSL hsl = HSL.RGBtoHSL(self.getRed(), self.getGreen(), self.getBlue());
        if (hsl.getL() < threshold) {
          newImage.setRGB(x, y, self.getRGB());
        } else {
          newImage.setRGB(x, y, Color.WHITE.getRGB());
        }
      }
    });
    return newImage;
  }


  //region去除边框
  /**
   *  去除边框
   * @param image
   * @return
   */
  public static BufferedImage RemoveBorder(final BufferedImage image)
  {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    //左上边框线条顶点
    final XPoint upperLeftBorderPoint=new XPoint();;
    //右下边框线条
    final XPoint bottomRightBorderPoint = new XPoint();;

    int width = image.getWidth();
    int height = image.getHeight();
    for(int x = 0; x<width;x++)
    {
      boolean isBreak = false;
      for(int y = 0; y<height;y++)
      {
        Pixel pixel = new Pixel(x,y,new Color(image.getRGB(x,y)));
        if(pixel.isColorPoint())
        {
//                    logger.debug("左上顶点:{}", pixel.toString(),x,y);
          int xLength = getHorizontalLine(image,x,y,false);
          int yLength = getVerticalLine(image,x,y,false);
          if(xLength>(width*0.7) && (yLength>(height*0.7)))
          {

            upperLeftBorderPoint.x = x;
            upperLeftBorderPoint.y = y;
            isBreak = true;
            break;
          }
        }
      }
      if(isBreak)
      {
        break;
      }
    }

    for(int x = width-1; x>0;x--)
    {
      boolean isBreak = false;
      for(int y = height-1; y>0;y--)
      {
        Pixel pixel = new Pixel(x,y,new Color(image.getRGB(x,y)));

        if( pixel.isColorPoint())
        {
//                    logger.debug("右下顶点:{}", pixel.toString());
          int xLength = getHorizontalLine(image,x,y,true);
          int yLength = getVerticalLine(image,x,y,true);
          if(xLength>(width*0.7) && (yLength>(height*0.7)))
          {

            bottomRightBorderPoint.x = x;
            bottomRightBorderPoint.y = y;
            isBreak = true;
            break;
          }
        }
      }
      if(isBreak)
      {
        break;
      }
    }

    proccessImage(image, new IPixelMatrixHandler() {
      @Override
      public void handle(Pixel self, PixelMatrix matrix) {
        int x = self.x;
        int y = self.y;

        if(x == upperLeftBorderPoint.x || y == upperLeftBorderPoint.y
            || x == bottomRightBorderPoint.x || y == bottomRightBorderPoint.y)
        {
          newImage.setRGB(x,y,Color.WHITE.getRGB());
        }
        else
        {
          newImage.setRGB(x,y,self.rgb.getRGB());
        }
      }
    });
    return newImage;
  }

  private static int getHorizontalLine(BufferedImage image,int xPoint,int yPoint,boolean reverse)
  {
    int length = 0;
    if(reverse)
    {
      for(int x = xPoint;x>0;x--)
      {
        Pixel pixel = new Pixel(x,yPoint,new Color(image.getRGB(x,yPoint)));
        if(pixel.isColorPoint())
        {
          length++;
        }
        else
        {
          break;
        }
      }
    }
    else
    {
      for(int x = xPoint;x<image.getWidth();x++)
      {
        Pixel pixel = new Pixel(x,yPoint,new Color(image.getRGB(x,yPoint)));
        if(pixel.isColorPoint())
        {
          length++;
        }
        else
        {
          break;
        }
      }
    }

    return length;

  }

  private static int getVerticalLine(BufferedImage image,int xPoint,int yPoint,boolean reverse)
  {
    int length = 0;
    if(reverse)
    {
      for(int y = yPoint;y>0;y--)
      {
        Pixel pixel = new Pixel(xPoint,y,new Color(image.getRGB(xPoint,y)));
        if(pixel.isColorPoint())
        {
          length++;
        }
        else
        {
          break;
        }
      }
    }
    else
    {
      for(int y = yPoint;y<image.getHeight();y++)
      {
        Pixel pixel = new Pixel(xPoint,y,new Color(image.getRGB(xPoint,y)));
        if(pixel.isColorPoint())
        {
          length++;
        }
        else
        {
          break;
        }
      }
    }

    return length;

  }
  //endregion

  //region 去除过于淡的像素点
  public static BufferedImage CleanGrayPoint(BufferedImage image, final int sThreshold, final int vThreshold)
  {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    proccessImage(image, new IPixelMatrixHandler() {
      @Override
      public void handle(Pixel self, PixelMatrix matrix) {
        int x = self.x;
        int y = self.y;
        if(self.hsv.V>vThreshold && self.hsv.S<sThreshold)
        {
          newImage.setRGB(x, y, Color.WHITE.getRGB());
        }
        else {
          newImage.setRGB(x, y, self.rgb.getRGB());
        }
      }
    });
    return newImage;
  }
  //endregion

  //region 调整对比度
  public static BufferedImage ConBrightness(BufferedImage src,float contrast,float brightness)
  {
    int width = src.getWidth();
    int height = src.getHeight();

    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
    int[] inPixels = new int[width*height];
    int[] outPixels = new int[width*height];
    src.getRGB( 0, 0, width, height, inPixels, 0, width );

    // calculate RED, GREEN, BLUE means of pixel
    int index = 0;
    int[] rgbmeans = new int[3];
    double redSum = 0, greenSum = 0, blueSum = 0;
    double total = height * width;
    for(int row=0; row<height; row++) {
      int ta = 0, tr = 0, tg = 0, tb = 0;
      for(int col=0; col<width; col++) {
        index = row * width + col;
        ta = (inPixels[index] >> 24) & 0xff;
        tr = (inPixels[index] >> 16) & 0xff;
        tg = (inPixels[index] >> 8) & 0xff;
        tb = inPixels[index] & 0xff;
        redSum += tr;
        greenSum += tg;
        blueSum +=tb;
      }
    }

    rgbmeans[0] = (int)(redSum / total);
    rgbmeans[1] = (int)(greenSum / total);
    rgbmeans[2] = (int)(blueSum / total);

    // adjust contrast and brightness algorithm, here
    for(int row=0; row<height; row++) {
      int ta = 0, tr = 0, tg = 0, tb = 0;
      for(int col=0; col<width; col++) {
        index = row * width + col;
        ta = (inPixels[index] >> 24) & 0xff;
        tr = (inPixels[index] >> 16) & 0xff;
        tg = (inPixels[index] >> 8) & 0xff;
        tb = inPixels[index] & 0xff;

        // remove means
        tr -=rgbmeans[0];
        tg -=rgbmeans[1];
        tb -=rgbmeans[2];

        // adjust contrast now !!!
        tr = (int)(tr * contrast);
        tg = (int)(tg * contrast);
        tb = (int)(tb * contrast);

        // adjust brightness
        tr += (int)(rgbmeans[0] * brightness);
        tg += (int)(rgbmeans[1] * brightness);
        tb += (int)(rgbmeans[2] * brightness);
        outPixels[index] = (ta << 24) | (clamp(tr) << 16) | (clamp(tg) << 8) | clamp(tb);
      }
    }
    setRGB( dest, 0, 0, width, height, outPixels );
    return dest;

  }

  private static int clamp(int value) {
    return value > 255 ? 255 :(value < 0 ? 0 : value);
  }
  //endregion

  public static BufferedImage cleanByLightness(BufferedImage image,final float lightness_threshold, final float saturation_threshold)
  {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    proccessImage(image, new IPixelMatrixHandler() {
      @Override
      public void handle(Pixel self, PixelMatrix matrix) {
        int x = self.x;
        int y = self.y;
        if (y == 4) {
          logger.info("x;{},y:{},v:{},s:{}", x, y, self.hsv.V, self.hsv.S);
        }
        if (self.hsv.V >= lightness_threshold && self.hsv.S <= 0.5
            || self.hsv.S <= saturation_threshold) {
          newImage.setRGB(x, y, Color.WHITE.getRGB());
        } else {
          newImage.setRGB(x, y, self.rgb.getRGB());
        }
      }
    });
    return newImage;
  }

  /**
   *  保留主要的颜色，将其他杂色区域涂白
   * @param image
   * @param totalColor 保留的颜色数
   * @param similarity   相似度（百分比）
   * @return
   */
  public static BufferedImage remainMainColor(BufferedImage image,final List<Color> colors, final int totalColor, final double similarity )
  {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    handleImage(image, new IPixelPointHandler() {
      @Override
      public void handle(int x, int y, Color self, Color upperLeft, Color upper, Color upperRight, Color left,
          Color right, Color bottomLeft, Color bottom, Color bottomRight) {

        boolean remain = false;
        for(int i = 0; (i<totalColor)&&(colors.size()>i);i++)
        {
          Color color = colors.get(i);
          if((colorValueComp(color,self)/MAX_RGB_COMP)<=similarity)
          {

            remain = true;
            break;
          }
        }
        if(remain)
        {
          newImage.setRGB(x,y,self.getRGB());
        }
        else
        {
          newImage.setRGB(x,y,Color.WHITE.getRGB());
        }
      }
    });

    return newImage;
  }

  /**
   *  将颜色和周边相差很大的像素点涂白
   * @param image
   * @param diffNum
   * @param similarity
   * @return
   */
  public  static BufferedImage removeColorNoise(BufferedImage image, final int diffNum, final double similarity) {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    handleImage(image, new IPixelPointHandler() {
      @Override
      public void handle(int x, int y, Color self, Color upperLeft, Color upper, Color upperRight, Color left,
          Color right, Color bottomLeft, Color bottom, Color bottomRight) {
        int sumComp = 0;
        sumComp += colorValueComp(self,upperLeft)/MAX_RGB_COMP>similarity?1:0;
        sumComp += colorValueComp(self,upper)/MAX_RGB_COMP>similarity?1:0;;
        sumComp += colorValueComp(self,upperRight)/MAX_RGB_COMP>similarity?1:0;;
        sumComp += colorValueComp(self,left)/MAX_RGB_COMP>similarity?1:0;
        sumComp += colorValueComp(self,right)/MAX_RGB_COMP>similarity?1:0;;
        sumComp += colorValueComp(self,bottomLeft)/MAX_RGB_COMP>similarity?1:0;;
        sumComp += colorValueComp(self,bottom)/MAX_RGB_COMP>similarity?1:0;;
        sumComp += colorValueComp(self,bottomRight)/MAX_RGB_COMP>similarity?1:0;;
        if(sumComp>diffNum)
        {
          newImage.setRGB(x,y,Color.WHITE.getRGB());
        }
        else
        {

          newImage.setRGB(x,y,self.getRGB());
        }

      }
    });
    return newImage;
  }

  /**
   *  去除经过二值化的图片噪点，将黑色噪点涂白
   * @param image
   * @param diffNum
   * @return
   */
  public static BufferedImage removeBlackNoise(BufferedImage image,final  int diffNum)
  {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    final double threshold = 0.8;
    handleImage(image, new IPixelPointHandler() {
      @Override
      public void handle(int x, int y, Color self, Color upperLeft, Color upper, Color upperRight, Color left, Color right, Color bottomLeft, Color bottom, Color bottomRight) {

        if(colorPercentComp(Color.WHITE,self)<0.5)
        {
          //白色返回
          newImage.setRGB(x,y,self.getRGB());
          return;
        }
        int diff = 0;
        //按顺时针遍历周边像素点
        diff += colorPercentComp(self,upper)>threshold?1:0;
        diff += colorPercentComp(self,upperRight)>threshold?1:0;
        diff += colorPercentComp(self,right)>threshold?diff+1:0;
        diff += colorPercentComp(self,bottomRight)>threshold?1:0;
        diff += colorPercentComp(self,bottom)>threshold?1:0;
        diff += colorPercentComp(self,bottomLeft)>threshold?1:0;
        diff += colorPercentComp(self,left)>threshold?1:0;
        diff += colorPercentComp(self,upperLeft)>threshold?1:0;

        logger.debug("diff:{}",diff);
        if(diff>=diffNum)
        {
          newImage.setRGB(x,y,Color.WHITE.getRGB());
        }
        else
        {
          newImage.setRGB(x,y,self.getRGB());
        }
      }
    });
    return newImage;
  }

  //根据str,font的样式以及输出文件目录
  public static BufferedImage createImage(String str, Font font) {
    //获取font的样式应用在str上的整个矩形
    Rectangle2D r = font.getStringBounds(str, new FontRenderContext(
        AffineTransform.getScaleInstance(1, 1), false, false));
    int unitHeight = (int) Math.floor(r.getHeight());//获取单个字符的高度
    //获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
    int width = (int) Math.round(r.getWidth()) + 1;
    int height = unitHeight + 3;//把单个字符的高度+3保证高度绝对能容纳字符串作为图片的高度
    //创建图片
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);//先用白色填充整张图片,也就是背景
    g.setColor(Color.black);//在换成黑色
    g.setFont(font);//设置画笔字体
    g.drawString(str, 0, font.getSize());//画出字符串
    g.dispose();
    return image;
  }

  public static BufferedImage merge(BufferedImage image1, BufferedImage image2) {

    int width1 = image1.getWidth();//图片宽度
    int height1 = image1.getHeight();//图片高度
    //从图片中读取RGB
    int[] ImageArrayOne = new int[width1*height1];
    ImageArrayOne = image1.getRGB(0,0,width1,height1,ImageArrayOne,0,width1);

    //对第二张图片做相同的处理
    int width2 = image2.getWidth();//图片宽度
    int height2 = image2.getHeight();//图片高度
    //从图片中读取RGB
    int[] ImageArrayTwo = new int[width2*height2];
    ImageArrayTwo = image2.getRGB(0,0,width2,height2,ImageArrayTwo,0,width2);

    //生成新图片
    int newWidth = width1>width2?width1:width2;
    int newHeight = height1+height2;
    BufferedImage ImageNew = new BufferedImage( newWidth,newHeight,BufferedImage.TYPE_INT_RGB);
    Graphics g = ImageNew.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, newWidth, newHeight);//先用白色填充整张图片,也就是背景
    ImageNew.setRGB(0, 0, width1, height1, ImageArrayOne, 0, width1);//设置上半部分的RGB
    ImageNew.setRGB(0,height1,width2,height2,ImageArrayTwo,0,width2);//设置下半部分的RGB

    return ImageNew;

  }

  /**
   *  去除图片中的白色边框
   * @param image
   * @return
   */
  public static BufferedImage RemoveWhiteBorder(BufferedImage image)
  {
    int width = image.getWidth();//图片宽度
    int height = image.getHeight();//图片高度
    int top_x =0;
    int top_y = 0;
    int bottom_x = width;
    int bottom_y = height;

    loopA:for(int x =0 ; x<width;x++) {
      for (int y = 0; y < height; y++){
//                logger.debug("x:{},y:{}",x,y);
        Color color =  new Color(image.getRGB(x,y));
        if(!color.equals(Color.WHITE))
        {
          top_x = x;
          top_y = y;
          break loopA;
        }
      }
    }

    loopB:for(int x =width-1 ; x>=0;x--) {
      for (int y = height-1; y >= 0; y--){

        Color color =  new Color(image.getRGB(x,y));
        if(!color.equals(Color.WHITE))
        {
          bottom_x = x;
          bottom_y = y;
          break loopB;
        }
      }
    }
    logger.debug("tx:{},ty:{},bx:{},by:{}",top_x,top_y,bottom_x,bottom_y);
    if(top_x>=bottom_x || top_x>=(width-1) || bottom_x<=0 || (top_x+bottom_x)>=width)
    {
      return image;
    }
    else
    {
      return  image.getSubimage(top_x,0,bottom_x,height);
    }

  }

  public static final void main(String[] args)throws Exception
  {
    File file = new File("C:\\plate\\temp\\20160717512207478.jpg");
    BufferedImage image = ImageIO.read(file);
    image = RemoveWhiteBorder(image);
    ImageIO.write(image,"png",new File("d:\\aaa.png"));

  }
}
