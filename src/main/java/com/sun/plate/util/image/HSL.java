package com.sun.plate.util.image;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.awt.*;

/**
 * Created by sun on 2015/12/30.
 */
public class HSL {

  /**
   * 色调
   */
  private int h = 0;
  /**
   * 饱和度
   */
  private int s = 0;
  /**
   * 深度
   */
  private int l = 0;

  public HSL() {
  }

  public HSL(int h, int s, int l) {
    setH(h);
    setS(s);
    setL(l);
  }

  public int getH() {
    return h;
  }

  public void setH(int h) {
    if (h < 0) {
      this.h = 0;
    } else if (h > 360) {
      this.h = 360;
    } else {
      this.h = h;
    }
  }

  public int getS() {
    return s;
  }

  public void setS(int s) {
    if (s < 0) {
      this.s = 0;
    } else if (s > 255) {
      this.s = 255;
    } else {
      this.s = s;
    }
  }

  public int getL() {
    return l;
  }

  public void setL(int l) {
    if (l < 0) {
      this.l = 0;
    } else if (l > 255) {
      this.l = 255;
    } else {
      this.l = l;
    }
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof HSL)) {
      return false;
    }
    HSL entity = (HSL) obj;
    return new EqualsBuilder().append(this.h, entity.getH()).append(this.s, entity.getS())
        .append(this.l, entity.getL()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.h).append(this.s).append(this.l).hashCode();
  }

  public String toString() {
    return "HSL {" + h + ", " + s + ", " + l + "}";
  }

  public static HSL RGBtoHSL(int red, int green, int blue) {

    float h = 0, s = 0, l = 0;
    // normalizes red-green-blue values

    float r = red / 255.f;

    float g = green / 255.f;

    float b = blue / 255.f;

    float maxVal = max3v(r, g, b);

    float minVal = min3v(r, g, b);

    // hue

    if (maxVal == minVal)

    {

      h = 0; // undefined

    } else if (maxVal == r && g >= b)

    {

      h = 60.0f * (g - b) / (maxVal - minVal);

    } else if (maxVal == r && g < b)

    {

      h = 60.0f * (g - b) / (maxVal - minVal) + 360.0f;

    } else if (maxVal == g)

    {

      h = 60.0f * (b - r) / (maxVal - minVal) + 120.0f;

    } else if (maxVal == b)

    {

      h = 60.0f * (r - g) / (maxVal - minVal) + 240.0f;

    }

    // luminance

    l = (maxVal + minVal) / 2.0f;

    // saturation

    if (l == 0 || maxVal == minVal)

    {

      s = 0;

    } else if (0 < l && l <= 0.5f)

    {

      s = (maxVal - minVal) / (maxVal + minVal);

    } else if (l > 0.5f)

    {

      s = (maxVal - minVal) / (2 - (maxVal + minVal)); //(maxVal-minVal > 0)?

    }

    float hue = (h > 360) ? 360 : ((h < 0) ? 0 : h);

    float saturation = ((s > 1) ? 1 : ((s < 0) ? 0 : s)) * 100;

    float luminance = ((l > 1) ? 1 : ((l < 0) ? 0 : l)) * 100;

    return new HSL(Math.round(hue), Math.round(saturation), Math.round(luminance));
  }

  public static Color HSLtoRGB(HSL hsl) {

    float h = hsl.getH();                  // h must be [0, 360]

    float s = hsl.getS() / 100.f; // s must be [0, 1]

    float l = hsl.l / 100.f;      // l must be [0, 1]

    float R, G, B;

    if (hsl.getS() == 0)

    {

      // achromatic color (gray scale)

      R = G = B = l * 255.f;

    } else

    {

      float q = (l < 0.5f) ? (l * (1.0f + s)) : (l + s - (l * s));

      float p = (2.0f * l) - q;

      float Hk = h / 360.0f;

      float[] T = new float[3];

      T[0] = Hk + 0.3333333f; // Tr   0.3333333f=1.0/3.0

      T[1] = Hk;              // Tb

      T[2] = Hk - 0.3333333f; // Tg

      for (int i = 0; i < 3; i++)

      {

        if (T[i] < 0) {
          T[i] += 1.0f;
        }

        if (T[i] > 1) {
          T[i] -= 1.0f;
        }

        if ((T[i] * 6) < 1)

        {

          T[i] = p + ((q - p) * 6.0f * T[i]);

        } else if ((T[i] * 2.0f) < 1) //(1.0/6.0)<=T[i] && T[i]<0.5

        {

          T[i] = q;

        } else if ((T[i] * 3.0f) < 2) // 0.5<=T[i] && T[i]<(2.0/3.0)

        {

          T[i] = p + (q - p) * ((2.0f / 3.0f) - T[i]) * 6.0f;

        } else {
          T[i] = p;
        }

      }

      R = T[0] * 255.0f;

      G = T[1] * 255.0f;

      B = T[2] * 255.0f;

    }

    int red = (int) ((R > 255) ? 255 : ((R < 0) ? 0 : R));

    int green = (int) ((G > 255) ? 255 : ((G < 0) ? 0 : G));

    int blue = (int) ((B > 255) ? 255 : ((B < 0) ? 0 : B));

    return new Color(red, green, blue);

  }

  private static float min3v(float v1, float v2, float v3) {
    return ((v1) > (v2) ? ((v2) > (v3) ? (v3) : (v2)) : ((v1) > (v3) ? (v3) : (v2)));
  }

  public static float max3v(float v1, float v2, float v3) {
    return ((v1) < (v2) ? ((v2) < (v3) ? (v3) : (v2)) : ((v1) < (v3) ? (v3) : (v1)));
  }
}
