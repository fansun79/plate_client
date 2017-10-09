package com.sun.plate.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by sun on 2015/8/24.
 */
public final class PlateUtil {

  public static boolean  isDebug = false;

  private  static  String DEFAULT_PLATE_HOME= "c:\\plate";

  public static File getPlateFile(String subFile)
  {
    File plateHome = null;
    if(!isDebug)
    {
      plateHome = getPlateHome();
    }
    else
    {
      URL url = PlateUtil.class.getResource("/");
      plateHome = new File(url.getPath());
    }
    if(subFile.startsWith(File.separator))
    {
      return new File(plateHome.getPath()+subFile);
    }
    else
    {
      return new File(plateHome.getPath()+File.separator+subFile);
    }
  }

  public  static File getPlateHome()
  {
    String  plateHome = System.getenv("PLATE_HOME");
    if(plateHome  ==  null  || plateHome.isEmpty())
    {
      plateHome = DEFAULT_PLATE_HOME;
    }
    return  new File(plateHome);
  }

  public static boolean isNumeric(String str){
    Pattern pattern = Pattern.compile("[0-9]*");
    return pattern.matcher(str).matches();
  }

  public static  java.util.Date getAutoQuoteTime(java.sql.Time time, int delay) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(time);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);

    calendar.setTime(new java.util.Date());
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);

    java.util.Date date = new java.util.Date(calendar.getTime().getTime() +delay);
    return date;
  }

  public static byte[] getBytes(BufferedImage image)
  {
    ByteArrayOutputStream out = null;
    try
    {
      out = new ByteArrayOutputStream();
      ImageIO.write(image, "PNG", out);
      return out.toByteArray();

    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    finally {
      try
      {
        if(out !=null)
        {
          out.close();
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

    }
    return null;
  }

  public static byte[] getBytes(String filePath){
    byte[] buffer = null;
    try {
      File file = new File(filePath);
      FileInputStream fis = new FileInputStream(file);
      ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
      byte[] b = new byte[1000];
      int n;
      while ((n = fis.read(b)) != -1) {
        bos.write(b, 0, n);
      }
      fis.close();
      bos.close();
      buffer = bos.toByteArray();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return buffer;
  }
}

