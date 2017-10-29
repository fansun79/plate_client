package com.sun.plate.captcha.handler;

import com.sun.plate.captcha.channelhanlder.CaptchaChannelHandler;
import com.sun.plate.captcha.CaptchaRequest;
import com.sun.plate.captcha.CaptchaResult;
import com.sun.plate.captcha.CaptchaTask;
import com.sun.plate.captcha.channelhanlder.impl.DelayedCaptchaChannelHandler;
import com.sun.plate.captcha.channelhanlder.impl.RealTimeCaptchaChannelHandler;
import com.sun.plate.util.PlateUtil;
import com.sun.plate.util.image.ImageUtil;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sun on 2017/10/16.
 */
public class CaptchaHandlerFactory {

  private static Logger logger = LoggerFactory.getLogger(CaptchaHandlerFactory.class);

  private static DefaultCaptchaHandler captchaHandler = null;

  private CaptchaHandlerFactory() {
  }

  ;

  public static CaptchaHanlder instance() {
    if (captchaHandler == null) {
      captchaHandler = new DefaultCaptchaHandler();
    }
    return captchaHandler;
  }


  private static class DefaultCaptchaHandler implements CaptchaHanlder {

    ConcurrentHashMap<String, ArrayBlockingQueue<CaptchaResult>> resultSyncQueueMap = new ConcurrentHashMap<String, ArrayBlockingQueue<CaptchaResult>>();

    @Override
    public CaptchaTask accept(CaptchaRequest reqest) {

      BufferedImage image = ImageUtil.merge(reqest.getCaptcha(), reqest.getPrompt());
      String hashToken = PlateUtil.GetBufferedImageHash(image);
      CaptchaTask task = new CaptchaTask();
      task.setCaptcha(image);
      task.setCaptchaToken(hashToken);
      task.setDelayed(reqest.isDelayed());
      task.setDelayTime(reqest.getDelayedTime());
      if (resultSyncQueueMap.contains(hashToken)) {
        logger.warn("验证码[{}]已正在处理。", hashToken);
        return task;
      } else {
        ArrayBlockingQueue<CaptchaResult> resultSyncQueue = new ArrayBlockingQueue<CaptchaResult>(1);
        resultSyncQueueMap.put(hashToken, resultSyncQueue);
        CaptchaChannelHandler executor = null;
        if (reqest.isDelayed()) {
          executor = new DelayedCaptchaChannelHandler();
        } else {
          executor = new RealTimeCaptchaChannelHandler();
        }
        executor.execute(task, resultSyncQueue);
      }

      return task;
    }

    @Override
    public CaptchaResult getResult(CaptchaTask task, long timeout) {

      ArrayBlockingQueue<CaptchaResult> synResult = resultSyncQueueMap.get(task.getCaptchaToken());
      if (synResult == null) {
        logger.warn("根据Token[{}]找不到相应的验证码请求", task.getCaptchaToken());
        return null;
      }
      try {
        CaptchaResult result = synResult.poll(timeout, TimeUnit.MILLISECONDS);
        return result;
      } catch (InterruptedException e) {
        logger.warn("Token[{}]获取验证码超时", task.getCaptchaToken(), e);
        return null;
      }


    }
  }

}
