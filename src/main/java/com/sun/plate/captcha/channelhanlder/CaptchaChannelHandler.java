package com.sun.plate.captcha.channelhanlder;

import com.sun.plate.captcha.CaptchaResult;
import com.sun.plate.captcha.CaptchaTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by sun on 2017/10/20.
 */
public interface CaptchaChannelHandler {

    void execute(CaptchaTask task,BlockingQueue<CaptchaResult> resultSyncQueue);

}
