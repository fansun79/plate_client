package com.sun.plate.captcha.channel;

import com.sun.plate.captcha.ChannelParam;
import com.sun.plate.captcha.CaptchaTask;
import com.sun.plate.captcha.channel.mock.MockCaptchaChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sun on 2017/10/22.
 */
public class CaptchaChannelFactory {

  public static List<CaptchaChannel> getChannelsByTask(CaptchaTask task) {
    List<CaptchaChannel> captchaChannels = new LinkedList<CaptchaChannel>();
    for (ChannelParam channelParam : task.getChannels()) {
      String channelName = channelParam.getChannelName();
      Map<String, String> params = channelParam.getParams();
      String content = params.get("content");
      String waitStr = params.get("wait");
      MockCaptchaChannel captchaChannel = new MockCaptchaChannel(content,Long.valueOf(waitStr));
      captchaChannels.add(captchaChannel);
    }
    return captchaChannels;
  }

}
