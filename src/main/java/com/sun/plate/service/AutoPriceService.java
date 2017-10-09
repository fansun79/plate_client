package com.sun.plate.service;

import com.sun.jna.platform.win32.WinDef;
import com.sun.plate.entity.xnative.NativeInfo;
import com.sun.plate.entity.xnative.XRect;
import com.sun.plate.mq.Producer;
import com.sun.plate.mq.message.PriceMessage;
import com.sun.plate.ocr.ImageRecognizer;
import com.sun.plate.ocr.MyImageRecognizer;
import com.sun.plate.util.PlateUtil;
import com.sun.plate.util.jna.NativeTool;
import java.awt.image.BufferedImage;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 自动读取价格
 * Created by sun on 2017/10/6.
 */
@Service
public class AutoPriceService {

  private static final Logger logger = LoggerFactory.getLogger(AutoPriceService.class);

  @Autowired
  private Producer producer;

  @Autowired
  private NativeInfo nativeInfo;

  @Autowired
  private ImageRecognizer recognizer;

  private Thread autoPriceThread;

  private int p = 100;


  public boolean isRunning() {
    return this.autoPriceThread != null && this.autoPriceThread.isAlive();
  }

  public void start(int interval, long duration,XRect rect) {
    if(!this.isRunning())
    {
      Date now = new Date();
      Date endTime = new Date(now.getTime()+duration);
      this.autoPriceThread = new Thread(new ReadPriceThread(interval,rect,endTime));
      this.autoPriceThread.start();
    }
    else
    {
      logger.info("自动价格读取线程已在运行");
    }
  }

  public void stop() {
    if(this.autoPriceThread != null && this.autoPriceThread.isAlive())
    {
      //如果自动出价线程正在运行，则关闭
      this.autoPriceThread.interrupt();
      this.autoPriceThread = null;
    }
    else
    {
      logger.info("自动价格读取线程已关闭");
    }
  }

  /**
   *
   */
  private class ReadPriceThread implements  Runnable
  {
    private int interval;

    private XRect priceRect;

    private int lastPrice = 0;

    private Date endTime;

    public ReadPriceThread(int interval,XRect priceRect,Date endTime)
    {
      this.interval = interval;
      this.priceRect = priceRect;
      this.endTime = endTime;
    }

    @Override
    public void run() {
      try {
        while (true)
        {
          try
          {
            PriceMessage msg =  new PriceMessage();
            int price = readPrice(nativeInfo.getIeFramehwnd(),priceRect);
            if(price>0 && price != lastPrice)
            {
              msg.setPrice(price);
              producer.send(PriceMessage.TOPIC,msg.toJSONString());
              lastPrice = price;
            }
          }
          catch(Exception e)
          {
            logger.error("读取价格错误",e);
          }

          Thread.currentThread().sleep(interval);
        }

      }
      catch (InterruptedException e)
      {
        logger.info("价格线程中断");
      }
      finally {
        logger.info("价格线程终止");
      }
    }

  }

  private Integer readPrice(WinDef.HWND hwnd,XRect priceRect)
  {
        logger.info("开始价格截图,X:{},Y:{},长{},宽{}", priceRect.x, priceRect.y,
                priceRect.width, priceRect.height);
//    try
//    {
//      BufferedImage priceImage = NativeTool.capture(hwnd, priceRect.x, priceRect.y,
//          priceRect.width, priceRect.height);
////            logger.debug("截图结束");
////            logger.debug("开始识别价格");
//      String result = recognizer.recognizeImage(priceImage, MyImageRecognizer.RECOGNIZE_TYPE_SMALLPRICE);
//      if(!PlateUtil.isNumeric(result))
//      {
//        return 0;
//      }
//      logger.debug("识别出价格：{}", result);
//
//      return Integer.parseInt(result.trim());
//    }
//    catch(Exception e)
//    {
//      logger.debug("读取价格错误", e);
//      return null;
//    }
    return p++;
  }





}
