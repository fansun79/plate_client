package com.sun.plate.mq.message;

import com.sun.plate.entity.xnative.XRect;

/**
 * Created by sun on 2017/10/7.
 */
public class StartAutoPriceCmd extends ServerPointMessage {

  public XRect rect = null;

  public int interval = 250;

  public long  duration = 100;

  public XRect getRect() {
    return rect;
  }

  public void setRect(XRect rect) {
    this.rect = rect;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }
}
