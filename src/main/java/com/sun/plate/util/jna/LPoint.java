package com.sun.plate.util.jna;

import com.sun.jna.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sun on 2015/6/21.
 */
public class LPoint extends Structure {

  @Override
  protected List getFieldOrder() {
    List a = new ArrayList();
    a.add("x");
    a.add("y");
    return a;
  }

  public static class ByReference extends LPoint implements Structure.ByReference {

  }

  ;

  public static class ByValue extends LPoint implements Structure.ByValue {

  }

  ;
  public Integer x;
  public Integer y;
}
