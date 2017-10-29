package com.sun.plate.util.image;


import java.awt.*;

/**
 * Created by sun on 2016/1/21.
 */
public class HSV {

  public final int H;

  public final int S;

  public final int V;

  public HSV(int h, int s, int v) {
    this.H = h;
    this.S = s;
    this.V = v;
  }

  public HSV(Color rgb) {
    float r = (float) rgb.getRed() / 255;
    float g = (float) rgb.getGreen() / 255;
    float b = (float) rgb.getBlue() / 255;
    float max = max(r, g, b);
    float min = min(r, g, b);
    float h = 0;
    if (r == max) {
      h = (g - b) / (max - min);
    }
    if (g == max) {
      h = 2 + (b - r) / (max - min);
    }
    if (b == max) {
      h = 4 + (r - g) / (max - min);
    }
    h *= 60;
    if (h < 0) {
      h += 360;
    }

    this.H = Math.round(h);
    this.S = Math.round(((max - min) / max) * 100);
    this.V = Math.round(max * 100);
  }

  private float max(float f1, float f2, float f3) {
    float max = f1;
    if (f2 > max) {
      max = f2;
    }
    if (f3 > max) {
      max = f3;
    }
    return max;
  }

  private float min(float f1, float f2, float f3) {
    float min = f1;
    if (f2 < min) {
      min = f2;
    }
    if (f3 < min) {
      min = f3;
    }
    return min;
  }


}

