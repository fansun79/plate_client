package com.sun.plate.cmd;

import com.alibaba.fastjson.annotation.JSONField;
import com.sun.jna.platform.win32.WinDef;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sun on 2017/10/10.
 */
public abstract class AbstractCommand implements Command {

  @JSONField(serialize = false)
  protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  private int beforeWait = 0;

  private int afterWait = 0;

  private int repeatWait = 0;

  private int repeat = 1;

  private int expire = 0;

  private String remark = "";

  @JSONField(serialize = false)
  private org.apache.log4j.Logger strategyLogger = null;

  @Override
  public WinDef.HWND getHwnd() {
    return hwnd;
  }

  public void setHwnd(WinDef.HWND hwnd) {
    this.hwnd = hwnd;
  }

  private WinDef.HWND hwnd;

  @Override
  public int getBeforeWait() {
    return this.beforeWait;
  }

  public void setBeforeWait(int beforeWait) {
    this.beforeWait = this.beforeWait;
  }

  @Override
  public int getAfterWait() {
    return this.afterWait;
  }

  @Override
  public int getRepeatWait() {
    return this.repeatWait;
  }

  @Override
  public int getRepeat() {
    return this.repeat;
  }

  @Override
  public int getExpire() {
    return this.expire;
  }

  @Override
  public String getRemark() {
    return this.remark;
  }

  public void setExpire(int expire) {
    this.expire = expire;
  }

  public void setRepeatWait(int repeatWait) {
    this.repeatWait = repeatWait;
  }

  public void setRepeat(int repeat) {
    this.repeat = repeat;
  }

  public void setAfterWait(int afterWait) {
    this.afterWait = afterWait;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public org.apache.log4j.Logger getStrategyLogger() {
    return strategyLogger;
  }

  public void setStrategyLogger(org.apache.log4j.Logger strategyLogger) {
    this.strategyLogger = strategyLogger;
  }

  @Override
  public void execute(CommandSession session) {
    try {
      if (this.getBeforeWait() > 0) {
        Thread.sleep(this.getBeforeWait());
      }
      int repeat = this.getRepeat();
      Date d1 = new Date();
      Date d2 = d1;

      int i = this.repeat;
      this.info("执行命令：" + this.remark);
      boolean isBreak = false;
      do {
        logger.debug("执行命令：{}", this.remark);

        try {
          isBreak = this.doExeute(session);
        } catch (Exception e) {
          logger.warn("执行命令发生异常：{}", this.remark, e);
          this.warn("执行命令发生异常：" + this.remark, e);
          isBreak = true;
        }
        if (this.getRepeatWait() > 0) {
          Thread.sleep(this.getRepeatWait());
        }
        d2 = new Date();
        i--;
//                logger.debug("i:{},expire:{},time:{},isBreak:{}",i,expire,d2.getTime()-d1.getTime(),isBreak);
      } while ((!isBreak) && ((repeat < 0) || (i > 0)) && ((this.expire <= 0)
          || (d2.getTime() - d1.getTime()) <= this.expire));

      if (this.getAfterWait() > 0) {
        Thread.sleep(this.getAfterWait());
      }
      d2 = new Date();
      this.info(
          "命令完毕：" + this.remark + ",耗时：" + Long.toString(d2.getTime() - d1.getTime()) + "毫秒，执行次数："
              + (repeat - i));

    } catch (Exception e) {
      logger.warn("执行出价命令错误", e);
    }

  }

  @Override
  public void info(String msg) {
    if (this.strategyLogger != null) {
      this.strategyLogger.info(msg);
    }
  }

  @Override
  public void warn(String msg, Throwable t) {
    if (this.strategyLogger != null) {
      this.strategyLogger.warn(msg, t);
    }
  }

  /**
   * 命令实际执行的部分，如果返回true,在外界有循环的情况下跳出循环
   */
  public abstract boolean doExeute(CommandSession session) throws Exception;
}
