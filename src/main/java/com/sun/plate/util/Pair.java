package com.sun.plate.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *  二元祖
 * Created by sun on 2015/12/31.
 */
public class Pair<A,B> {
  public final A a;
  public final B b;

  public Pair(A a,B b)
  {
    this.a = a;
    this.b = b;
  }

  public static Pair<Integer,Integer> intPair(Integer a,Integer b)
  {
    return new Pair<Integer,Integer>(a,b);
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(this == obj)
    {
      return true;
    }
    if(!(obj instanceof Pair))
    {
      return false;
    }
    Pair entity = (Pair)obj;
    return new EqualsBuilder()
        .append(this.a, entity.a)
        .append(this.b, entity.b)
        .isEquals();
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder(17, 37)
        .append(this.a)
        .append(this.b)
        .hashCode();
  }

  @Override
  public String  toString()
  {
    return "Pair {" + a + ", " + b + "}";
  }
}
