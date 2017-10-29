package com.sun.plate.captcha.channel.zhima;

import com.sun.plate.captcha.CaptchaTask;
import com.sun.plate.captcha.channel.CaptchaChannel;
import com.sun.plate.captcha.channel.zhima.support.LogTypeEnum;
import java.io.File;

/**
 * Created by sun on 2017/10/28.
 */
public class ZhimaCaptchaChannel implements CaptchaChannel {

  private  static final String	USERNAME	  =  "wooizzmsn";
  private static final String	PASSWORD  = "Y209fD8j";

  @Override
  public String getCaptcha(CaptchaTask task) {
    File imageFileTest = new File("/Users/sun/file_private/testcaptcha.png");
    String recognizeResult = HttpApiClient.recognize(
        "1877",								// 软件ID. 如何获取? (参见 http://www.zhima365.com/jump/api_help_software.php)
        "3d218a992b794f4fc7d032473bcf70d2", // 软件key. 如何获取? (参见 http://www.zhima365.com/jump/api_help_software.php)
        USERNAME, 							// 需要注册用户, 然后联系客服QQ1766515174充入免费测试题分. 注册地址: http://www.zhima365.com/api.php?id=464
        PASSWORD,							// 用户的密码
        imageFileTest, 						// 需要上传的文件
        "6003", 							// 图片类型. 图片类型是什么? (参见 http://www.zhima365.com/jump/api_help_picture_type.php)
        120, 								// 设置超时时间(单位:秒)
        "请按图片中的规则识别验证码", 								// 备注
        LogTypeEnum.YES);					// 是否记录日志
    System.out.println("识别结果：" + recognizeResult);
    return recognizeResult;

  }

  @Override
  public String getChannelName() {
    return "zhima";
  }


  public static final void main(String[] args)
  {
    ZhimaCaptchaChannel channel = new ZhimaCaptchaChannel();
    channel.getCaptcha(null);
  }
}
