package com.sun.plate.captcha.channelhanlder.impl;

import com.alibaba.fastjson.JSON;
import com.sun.plate.captcha.CaptchaResult;
import com.sun.plate.captcha.CaptchaTask;
import com.sun.plate.captcha.ChannelParam;
import com.sun.plate.captcha.channel.CaptchaChannel;
import com.sun.plate.captcha.channel.CaptchaChannelFactory;
import com.sun.plate.captcha.channelhanlder.CaptchaChannelHandler;
import com.sun.plate.captcha.channelhanlder.CaptchaChannelResult;
import com.sun.plate.util.PlateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 延迟获取
 * Created by sun on 2017/10/21.
 */
public class DelayedCaptchaChannelHandler implements CaptchaChannelHandler {

  private static final Logger logger = LoggerFactory.getLogger(DelayedCaptchaChannelHandler.class);

  private static final int CHANNELS = 20;

  private List<CaptchaChannelResult> results = Collections
      .synchronizedList(new LinkedList<CaptchaChannelResult>());

  @Override
  public void execute(CaptchaTask task, BlockingQueue<CaptchaResult> resultSyncQueue) {

    List<CaptchaChannel> channels = CaptchaChannelFactory.getChannelsByTask(task);
    CountDownLatch latch = new CountDownLatch(channels.size());
    try {

      for (CaptchaChannel channel : channels) {
        Thread thread = new Thread(new CaptchaChannelThread(latch, channel, task));
        thread.start();
      }
      latch.await(task.getDelayTime(), TimeUnit.MILLISECONDS);

    } catch (InterruptedException e) {
      logger.warn("等待被中断", e);
    }

    CaptchaResult result = getCaptcha();
    if (result != null) {
      logger.debug("验证码选取:{}", JSON.toJSONString(result));
      resultSyncQueue.offer(result);
      logger.debug("验证码已插入");
//      try
//      {
//        resultSyncQueue.offer(result);
//        logger.debug("验证码已插入");
//      }
//      catch (InterruptedException e)
//      {
//        logger.warn("插入队列超时");
//      }
    }
  }

  /**
   * 计算验证码, 有多个不同的验证码，根据策略返回可能是正确答案的一个
   * 验证码优先级
   * 1.不符合验证码长度，且包含在其他验证码中 +10
   * 2.不符合验证码长度，且有相同验证码 +100
   * 3.符合验证码长度，且包含在其他验证码中 +1000
   * 4.符合验证码长度，且有相同验证码 +10000
   * 5. 符合验证码长度 +1
   * 6. 如果是数字 +5
   * 7.如果是手动渠道，且符合验证码长度 +9999999
   */
  private CaptchaResult getCaptcha() {
    logger.debug("获取时的值：{}", JSON.toJSONString(this.results));
    int maxPriority = -1;
    CaptchaChannelResult selectedResult = null;
    for (CaptchaChannelResult r1 : this.results) {
      int priority = 0;
      String content = r1.getResult();
      if (PlateUtil.isNumeric(content)) {
        priority += 5;
      }

      /**
       * 如果是手动渠道，且符合验证码长度 +9999999
       */
      if ("手动".equalsIgnoreCase(r1.getChannel())) {
        priority += 9999999;
      }

      for (CaptchaChannelResult r2 : this.results) {
        if (r2.getResult().equalsIgnoreCase(content) && r1 != r2) {
          priority += (CHANNELS * CHANNELS);
        } else if (r2.getResult().contains(content) && r1 != r2) {
          priority += (CHANNELS);
        } else {
          priority += 1;
        }

      }
      logger.debug("验证码：{}，渠道：{}, 分数：{}", r1.getResult(), r1.getChannel(), priority);
      if (priority > maxPriority) {
        maxPriority = priority;
        selectedResult = r1;
      }
    }
    if (selectedResult != null) {
      CaptchaResult result = new CaptchaResult();
      result.setChannel(selectedResult.getChannel());
      result.setResult(selectedResult.getResult());
      result.setTime(selectedResult.getTime());
      result.setResults(this.results);
      return result;
    } else {
      return null;
    }

  }


  private class CaptchaChannelThread implements Runnable {

    private CountDownLatch latch;

    private CaptchaChannel channel;

    private CaptchaTask task;

    public CaptchaChannelThread(CountDownLatch latch, CaptchaChannel channel, CaptchaTask task) {
      this.latch = latch;
      this.channel = channel;
      this.task = task;
    }

    @Override
    public void run() {
      try {
        long beginTime = System.currentTimeMillis();
        String captchaContent = this.channel.getCaptcha(this.task);
        long endTime = System.currentTimeMillis();
        String channelName = this.channel.getChannelName();
        CaptchaChannelResult result = new CaptchaChannelResult();
        result.setChannel(channelName);
        result.setResult(captchaContent);
        result.setTime(endTime - beginTime);
        results.add(result);
      } finally {
        this.latch.countDown();
      }


    }
  }


  public static final void main(String[] args) throws Exception {
    CaptchaTask task = new CaptchaTask();
    task.setDelayed(true);
    task.setDelayTime(1000);
    task.setCaptchaToken("123");
    task.setCaptcha(null);
    List<ChannelParam> list = new ArrayList<ChannelParam>();
    list.add(new ChannelParam("mock", new ImmutablePair<String, String>("content", "123"),
        new ImmutablePair<String, String>("wait", "2000")));
    list.add(new ChannelParam("mock", new ImmutablePair<String, String>("content", "321"),
        new ImmutablePair<String, String>("wait", "100")));
    list.add(new ChannelParam("mock", new ImmutablePair<String, String>("content", "123"),
        new ImmutablePair<String, String>("wait", "2000")));
    task.setChannels(list);

    BlockingQueue<CaptchaResult> result = new ArrayBlockingQueue<CaptchaResult>(1);
    DelayedCaptchaChannelHandler handler = new DelayedCaptchaChannelHandler();
    handler.execute(task, result);
    CaptchaResult r = result.poll();
    System.out.println(r.getResult());

  }


}
