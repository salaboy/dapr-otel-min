package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class CounterService {
  private int counter = 0;

  public void increment() {
    this.counter++;
  }

  public int getCounter() {
    return this.counter;
  }

  public void reset() {
    this.counter = 0;
  }
}
