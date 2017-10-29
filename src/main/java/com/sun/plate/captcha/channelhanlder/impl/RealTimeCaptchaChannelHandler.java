package com.sun.plate.captcha.channelhanlder.impl;

import com.alibaba.fastjson.JSON;
import com.sun.plate.captcha.CaptchaResult;
import com.sun.plate.captcha.CaptchaTask;
import com.sun.plate.captcha.ChannelParam;
import com.sun.plate.captcha.channel.CaptchaChannel;
import com.sun.plate.captcha.channel.CaptchaChannelFactory;
import com.sun.plate.captcha.channelhanlder.CaptchaChannelHandler;
import com.sun.plate.captcha.channelhanlder.CaptchaChannelResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sun on 2017/10/21.
 */
public class RealTimeCaptchaChannelHandler implements CaptchaChannelHandler {

  private static final Logger logger = LoggerFactory.getLogger(RealTimeCaptchaChannelHandler.class);

  @Override
  public void execute(CaptchaTask task, BlockingQueue<CaptchaResult> resultSyncQueue) {

    List<CaptchaChannel> channels = CaptchaChannelFactory.getChannelsByTask(task);
    if (channels != null && channels.isEmpty()) {
      return;
    }
    ArrayBlockingQueue<CaptchaChannelResult> queue = new ArrayBlockingQueue(channels.size());
    ArrayBlockingQueue<CaptchaResult> singleResultQueue = new ArrayBlockingQueue<CaptchaResult>(1);
    List<CaptchaChannelResult> results = Collections
        .synchronizedList(new LinkedList<CaptchaChannelResult>());
    ExecutorService executorService = Executors.newFixedThreadPool(channels.size());
    for (CaptchaChannel channel : channels) {
      executorService.submit(new CaptchaChannelThread(queue, channel, task));
    }
    executorService.submit(new CaptchaResultThread(task, singleResultQueue, queue, results));

    try {
      CaptchaResult captchaResult = singleResultQueue
          .poll(task.getDelayTime(), TimeUnit.MILLISECONDS);
      if(captchaResult == null)
      {
        if (results != null && !results.isEmpty()) {
          CaptchaResult result = new CaptchaResult();
          result.setChannel(results.get(0).getChannel());
          result.setResult(results.get(0).getResult());
          result.setTime(results.get(0).getTime());
          result.setResults(results);
          resultSyncQueue.offer(result);
        }
      }
      else
      {
        resultSyncQueue.offer(captchaResult);
      }

      return;
    } catch (InterruptedException e) {
      logger.debug("等待验证码超时");
      if (results != null && !results.isEmpty()) {
        CaptchaResult result = new CaptchaResult();
        result.setChannel(results.get(0).getChannel());
        result.setResult(results.get(0).getResult());
        result.setTime(results.get(0).getTime());
        result.setResults(results);
        resultSyncQueue.offer(result);
      }
    }
    finally {
      executorService.shutdownNow();
    }


  }

  public CaptchaResult getCaptchaForPoll(List<CaptchaChannelResult> results) {
    logger.debug("获取时的值：{}", JSON.toJSONString(results));
    Set<String> set = new HashSet<String>(); //重复验证码
    CaptchaChannelResult selectedResult = null;
    for (CaptchaChannelResult r1 : results) {
      if (r1 == null || StringUtils.isBlank(r1.getResult())) {
        continue;
      }
      if (r1.getChannel().equals("手动")) {
        selectedResult = r1;
      } else {
        if (set.contains(r1.getResult())) {
          selectedResult = r1;
        } else {
          set.add(r1.getResult());
        }
      }
    }
    if (selectedResult == null) {
      return null;
    } else {
      CaptchaResult result = new CaptchaResult();
      result.setChannel(selectedResult.getChannel());
      result.setResult(selectedResult.getResult());
      result.setTime(selectedResult.getTime());
      result.setResults(results);
      return result;
    }
  }

  private class CaptchaChannelThread implements Runnable {

    private BlockingQueue<CaptchaChannelResult> queue;

    private CaptchaChannel channel;

    private CaptchaTask task;

    public CaptchaChannelThread(BlockingQueue<CaptchaChannelResult> queue, CaptchaChannel channel,
        CaptchaTask task) {
      this.queue = queue;
      this.channel = channel;
      this.task = task;
    }

    @Override
    public void run() {
      long beginTime = System.currentTimeMillis();
      String captchaContent = this.channel.getCaptcha(this.task);
      long endTime = System.currentTimeMillis();
      String channelName = this.channel.getChannelName();
      CaptchaChannelResult result = new CaptchaChannelResult();
      result.setChannel(channelName);
      result.setResult(captchaContent);
      result.setTime(endTime - beginTime);
      this.queue.offer(result);
    }
  }

  private class CaptchaResultThread implements Runnable {

    private CaptchaTask task = null;

    private ArrayBlockingQueue<CaptchaResult> singleResultQueue;

    private ArrayBlockingQueue<CaptchaChannelResult> channelResults;

    private List<CaptchaChannelResult> results;

    public CaptchaResultThread(CaptchaTask task,
        ArrayBlockingQueue<CaptchaResult> singleResultQueue,
        ArrayBlockingQueue<CaptchaChannelResult> channelResults,
        List<CaptchaChannelResult> results) {
      this.task = task;
      this.singleResultQueue = singleResultQueue;
      this.channelResults = channelResults;
      this.results = results;
    }

    @Override
    public void run() {

      try {
        CaptchaResult result = null;
        long timeout = this.task.getDelayTime();
        while (result == null && !Thread.interrupted()) {
          long beginTime = System.currentTimeMillis();
          CaptchaChannelResult channelResult = channelResults.take();
          this.results.add(channelResult);
          result = getCaptchaForPoll(this.results);
          long endTime = System.currentTimeMillis();
          timeout -= (endTime - beginTime);
          if (result != null) {
            singleResultQueue.offer(result);
            return;
          }
        }
      } catch (InterruptedException e) {
        logger.warn("等待结果超时", e);
      }
    }
  }


  public static final void main(String[] args) {
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
    RealTimeCaptchaChannelHandler handler = new RealTimeCaptchaChannelHandler();
    handler.execute(task, result);
    CaptchaResult r = result.poll();
    System.out.println(r.getResult());
  }
}
