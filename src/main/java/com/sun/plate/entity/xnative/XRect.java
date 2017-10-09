package com.sun.plate.entity.xnative;




/**
 * Created by sun on 2015/11/14.
 */
public class XRect {


    public int x = 0;


    public int y = 0;


    public int width = 0;


    public int height = 0;

    public XRect(){}

    public XRect(int x, int y, int width,int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

//    public Rect toRect()
//    {
//         Rect  rect = new Rect();
//         rect.x = this.x;
//         rect.y = this.y;
//         rect.w = this.width;
//         rect.h = this.height;
//         return rect;
//    }


}
