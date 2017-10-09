package com.sun.plate.ocr;


import com.sun.plate.ocr.filter.ImagePreprocessingFilter;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Created by sun on 2015/7/7.
 */
@Service
public class MyImageRecognizer extends AbstractImageRecognizer {

  public static final String RECOGNIZE_TYPE_SMALLPRICE = "smallprice";

  public static final String RECOGNIZE_TYPE_CONFIRM_BUTTON = "confirm";

  public static final String RECOGNIZE_TYPE_TITLE = "title";

  public static final String RECOGNIZE_TYPE_BUTTON = "button";


  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MyImageRecognizer.class);

  private List<TradingImage> confirmButtonImages = new ArrayList<TradingImage>();

  private List<TradingImage> smallpriceImages = new ArrayList<TradingImage>();

  private List<TradingImage> titleImages = new ArrayList<TradingImage>();

  private List<TradingImage> buttonImages = new ArrayList<TradingImage>();

  public MyImageRecognizer()
  {
    super();

    this.addImagePreprocessingFilter(RECOGNIZE_TYPE_CONFIRM_BUTTON, new MyPreprocessingFilter(700));
    this.addImagePreprocessingFilter(RECOGNIZE_TYPE_BUTTON,new MyPreprocessingFilter(700));
    this.addImagePreprocessingFilter(RECOGNIZE_TYPE_SMALLPRICE,new MyPreprocessingFilter(400));
    this.addImagePreprocessingFilter(RECOGNIZE_TYPE_TITLE,new MyPreprocessingFilter(700));

    //加载确认按钮
    this.reloadTradingImages(this.confirmButtonImages,RECOGNIZE_TYPE_CONFIRM_BUTTON);
    //加载刷新按钮
    this.reloadTradingImages(this.buttonImages,RECOGNIZE_TYPE_BUTTON);
    //加载价格数字
    this.reloadTradingImages(this.smallpriceImages,RECOGNIZE_TYPE_SMALLPRICE);
    //加载窗口标题
    this.reloadTradingImages(this.titleImages,RECOGNIZE_TYPE_TITLE);

  }

  private void reloadTradingImages(List<TradingImage> images,String dirName)
  {
    List<TradingImage> list =  this.tradingImageMap.get(dirName);
    if(list != null && !list.isEmpty())
    {
      ImagePreprocessingFilter filter = this.getImagePreprocessingFilter(dirName);
      for(TradingImage  tradingImage : list)
      {
        BufferedImage img = tradingImage.image;
        if(filter !=null)
        {
          img = filter.doProcess(img);
        }
        TradingImage new_tradingImage = new TradingImage(img,tradingImage.value);
        images.add(new_tradingImage);
      }

    }
  }

  public String recognizeImage(BufferedImage image,String key,Integer n)
  {
    ImagePreprocessingFilter filter = this.getImagePreprocessingFilter(key);
    try {
      if(RECOGNIZE_TYPE_SMALLPRICE.equalsIgnoreCase(key))
      {
        //解析价格
        return this.scan(image,filter,this.smallpriceImages,n);
      }
      else if(RECOGNIZE_TYPE_TITLE.equalsIgnoreCase(key))
      {
        //解析窗口标题
        return this.scan(image,filter,this.titleImages,n);
      }
      else if( RECOGNIZE_TYPE_CONFIRM_BUTTON.equalsIgnoreCase(key))
      {
        //解析确认按钮
        StringBuilder sb  = new StringBuilder();
        BufferedImage img = filter.doProcess(image);
        String result = getSingleCharOcr(img,this.confirmButtonImages);
        return result;

      }
      else if( RECOGNIZE_TYPE_BUTTON.equalsIgnoreCase(key))
      {
        //解析刷新按钮
        StringBuilder sb  = new StringBuilder();
        BufferedImage img = filter.doProcess(image);
        String result = getSingleCharOcr(img,this.buttonImages);
        return result;

      }
      else
      {
        throw new java.lang.UnsupportedOperationException("识别类型("+key+")暂不支持");
      }
    }
    catch(Exception e)
    {
      logger.debug("识别图形错误",e);
      return "";
    }

  }

  private String scan(BufferedImage image,  ImagePreprocessingFilter filter,List<TradingImage> tradingImages,Integer n)
  {
    StringBuilder sb  = new StringBuilder();
    BufferedImage img = filter.doProcess(image);
    //分割图片
    java.util.List<BufferedImage> imgList = splitImage(img);
    int i =0;
    for(BufferedImage bi : imgList)
    {
      //识别单个图片
      //debug 单个分割后的图片
      this.writeDebugImage(bi,"分割");
      //识别单个图片内容
      String c = getSingleCharOcr(bi,tradingImages);
      sb.append(c);
      if(n != null &&n>=0 && i>=n)
      {
        return sb.toString();
      }
      i++;
    }
    return sb.toString();
  }

  //按空白分割分割图片
  protected static List<BufferedImage> splitImage(BufferedImage img)
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

  public  String getSingleCharOcr(BufferedImage img, List<TradingImage> list) {
    int width = img.getWidth();  //图片宽度
    int height = img.getHeight(); //图片高度
    int min = width * height;  //图片面积
    int maxCount = 0;
    String  value = null;
    for(TradingImage tradingImage : list)
    {
      int count = 0;
      BufferedImage image = tradingImage.image;
      int widthmin = width < image.getWidth() ? width : image.getWidth();
      int heightmin = height < image.getHeight() ? height : image.getHeight();
      for (int x = 0; x < widthmin; ++x) {
        for (int y = 0; y < heightmin; ++y) {
          if (img.getRGB(x, y) == image.getRGB(x, y)) {
            count++;
            if (count >= min)
              return tradingImage.value;
          }
        }
      }
      if(count>maxCount)
      {
        maxCount = count;
        value = tradingImage.value;
      }
    }
    double  ratio =  (double)maxCount / (double)min ;
//        logger.debug("" + ratio);
    if(ratio<0.7d)
    {
      return "";
    }
    else
    {
      return value;
    }
  }
  public static BufferedImage toBlackWhiteImage(BufferedImage image,int whiteValue)
  {
    int width = image.getWidth();
    int height = image.getHeight();
    int minWidth = -1;
    int maxWidth = -1;
    int minHeight = -1;
    int maxHeight = -1;
    for(int x = 0 ; x<width;x++)
    {
      for(int y = 0 ; y<height;y++)
      {
        if(isWhite(image.getRGB(x,y),whiteValue))
        {
          image.setRGB(x, y, Color.WHITE.getRGB());
        }
        else
        {
          image.setRGB(x, y, Color.BLACK.getRGB());
          if(maxWidth<0 && minWidth<0)
          {
            maxWidth = x;
            minWidth = x;
          }
          if(minHeight<0 && maxHeight<0)
          {
            minHeight = y;
            maxHeight = y;
          }

          if(x<minWidth)
          {
            minWidth = x;
          }
          if(x>maxWidth)
          {
            maxWidth = x;
          }
          if(y<minHeight)
          {
            minHeight = y;
          }
          if(y>maxHeight)
          {
            maxHeight = y;
          }
        }
      }
    }

    int sub_x =  minWidth>width?width:minWidth;
    int sub_y =  minHeight>height?height:minHeight;
    int sub_w = (maxWidth - minWidth + 1)>width?width:(maxWidth - minWidth + 1);
    int sub_h =  ( maxHeight - minHeight + 1)>height?height:( maxHeight - minHeight + 1);
//        System.out.println(sub_x);
//        System.out.println(sub_y);
//        System.out.println(sub_w);
//        System.out.println(sub_h);
    if(sub_x >=0 && sub_y>=0 && sub_w>=0 && sub_h>=0)
    {
      return image.getSubimage(sub_x, sub_y, sub_w, sub_h);
    }
    else
    {
//            throw new java.lang.IllegalArgumentException("图片可能是一个空白页面");
      logger.debug("图片可能是一个空白页面");
      return image;
    }

  }


  public static boolean isBlack(int colorInt,int whiteValue)
  {
    Color color = new Color(colorInt);
    if (color.getRed() + color.getGreen() + color.getBlue() <= whiteValue)
    {
      return true;
    }
    return false;
  }

  public static boolean isWhite(int colorInt,int whiteValue)
  {
    Color color = new Color(colorInt);
    if (color.getRed() + color.getGreen() + color.getBlue() > whiteValue)
    {
      return true;
    }
    return false;
  }


  @Override
  protected String getTradingDataDirName() {
    return "my";
  }


  private class MyPreprocessingFilter implements ImagePreprocessingFilter
  {
    private int whiteValue  = 0;

    public MyPreprocessingFilter(int whiteValue)
    {
      this.whiteValue = whiteValue;
    }

    @Override
    public BufferedImage doProcess(BufferedImage image) {
      BufferedImage img =  toBlackWhiteImage(image,whiteValue);
      writeDebugImage(img, "黑白化");
      return img;
    }
  }


  public final static void main(String[] args)throws Exception
  {

    MyImageRecognizer  r = new MyImageRecognizer();
    r.setIsDebug(true);
    BufferedImage img = ImageIO.read(new File("/Users/sun/file_private/plate_old/plate/src/main/bin/testsmallprice/76700.jpg"));
    Date t1 = new Date();
    String result = r.recognizeImage(img, "smallprice", null);
    Date t2 = new Date();
    System.out.println(t2.getTime()-t1.getTime());
    System.out.println(result);


  }


}

