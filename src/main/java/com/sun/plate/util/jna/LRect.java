package com.sun.plate.util.jna;

import com.sun.jna.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sun on 2015/6/21.
 */
public class LRect extends Structure{
  public int    left;
  public int    top;
  public int   right;
  public int bottom;

  @Override
  protected List getFieldOrder() {
    List a = new ArrayList();
    a.add("left");
    a.add("top");
    a.add("right");
    a.add("bottom");
    return a;
  }

  public static class ByReference extends LRect implements Structure.ByReference {};
  public static class ByValue extends LRect implements Structure.ByValue{};
}

