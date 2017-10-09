package com.sun.plate.mq.message;

/**
 * Created by sun on 2017/10/7.
 */
public class PriceMessage extends ClientPointMessage {

  public static final String TOPIC = "topic_price";

  private int price;

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }


}
