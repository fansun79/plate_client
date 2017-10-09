package com.sun.plate.entity.xnative;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by sun on 2015/11/14.
 */
public class XPoint {


    public int x;

    public int y;

    public XPoint(){}

    public XPoint(int x,int y)
    {
        this.x = x;
        this.y = y;
    }

//    public Point toPoint()
//    {
//         Point point = new Point();
//         point.x = x;
//         point.y = y;
//         return point;
//    }

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
        if(!(obj instanceof XPoint))
        {
            return false;
        }
        XPoint entity = (XPoint)obj;
        return new EqualsBuilder()
                .append(this.x, entity.x)
                .append(this.y,entity.y)
                .isEquals();

    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(this.x)
                .append(this.y)
                .hashCode();
    }
    @Override
    public String toString()
    {
        return this.x+":"+this.y;
    }
}
