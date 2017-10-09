package com.sun.plate;


import com.sun.plate.mq.impl.JeroMQServer;
import org.springframework.boot.SpringApplication;


public class PlateClientApplicationTest
{

  public static void main(String[] args) {
    JeroMQServer server = new JeroMQServer();
    server.setIoThreads(10);
    server.setPubPort(6001);
    server.setRespPort(6000);
    server.start();
  }
}
