package com.sun.plate.ocr;

/**
 * Created by sun on 2017/10/6.
 */

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检测训练图像
 * Created by sun on 2015/8/26.
 */
public class TradingImage {

  private static final Logger logger = LoggerFactory.getLogger(TradingImage.class);

  public final BufferedImage image;

  public final String value;

  public TradingImage(BufferedImage image, String value) {
    this.image = image;
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TradingImage)) {
      return false;
    }
    TradingImage entity = (TradingImage) obj;
    return new EqualsBuilder().append(this.image, entity.image).isEquals();

  }

  @Override
  public int hashCode() {

    return new HashCodeBuilder(17, 37).append(this.image).hashCode();
  }


  /**
   * 读取资源文件里的训练图片
   */
  public static Map<String, List<TradingImage>> loadAll()
  {

    Map<String,List<TradingImage>> map = new HashMap<String,List<TradingImage>>();
    /**
     * button
     */
    putFile("button","captchaing","png",map);
    putFile("button","quote","jpg",map);
    putFile("button","reload","jpg",map);

    /**
     * captcha
     */
    putFile("captcha","0","png",map);
    putFile("captcha","1","png",map);
    putFile("captcha","2","png",map);
    putFile("captcha","3","png",map);
    putFile("captcha","4","png",map);
    putFile("captcha","5","png",map);
    putFile("captcha","6","png",map);
    putFile("captcha","7","png",map);
    putFile("captcha","8","png",map);
    putFile("captcha","9","png",map);
    /**
     * confirm
     */
    putFile("confirm","ok","jpg",map);
    /**
     * smallprice
     */
    putFile("smallprice","0","jpg",map);
    putFile("smallprice","1","jpg",map);
    putFile("smallprice","2","jpg",map);
    putFile("smallprice","3","jpg",map);
    putFile("smallprice","4","jpg",map);
    putFile("smallprice","5","jpg",map);
    putFile("smallprice","6","jpg",map);
    putFile("smallprice","7","jpg",map);
    putFile("smallprice","8","jpg",map);
    putFile("smallprice","9","jpg",map);
    /**
     * title
     */
    putFile("title","mai","jpg",map);
    putFile("title","tou","jpg",map);
    putFile("title","pai","jpg",map);
    putFile("title","ti","jpg",map);
    putFile("title","biao","jpg",map);
    putFile("title","shi","jpg",map);
    putFile("title","xi","jpg",map);
    putFile("title","tong","jpg",map);

    return map;
  }


  private static void putFile(String dirName,String fileName,String fileType,Map<String,List<TradingImage>> map)
  {
      try
      {
        URL url = TradingImage.class.getResource("/tradingdata/my/"+dirName+"/"+fileName+"."+fileType);
        logger.debug(url.getFile());
        File imageFile = new File(url.getFile());
        BufferedImage image = ImageIO.read(imageFile);
        TradingImage tradingImg = new TradingImage(image, fileName);

        List<TradingImage> images = null;
        if(map.containsKey(dirName))
        {
          images = map.get(dirName);

        }
        else
        {
          images = new ArrayList<TradingImage>();
          map.put(dirName,images);
        }
        images.add(tradingImg);
      }
      catch (Exception e)
      {
        logger.error("加载训练数据{0}错误", fileName, e);
      }

}


  public static final void main(String[] args)
  {
     loadAll();
  }

}
