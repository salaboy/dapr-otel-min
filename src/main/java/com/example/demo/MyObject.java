package com.example.demo;

public class MyObject {
  private String name;
  private int counter;
  private MyNestedObject myNestedObject;
  private MyEnum myEnum;

  public MyObject(String name, int counter, MyNestedObject myNestedObject, MyEnum myEnum) {
    this.name = name;
    this.counter = counter;
    this.myNestedObject = myNestedObject;
    this.myEnum = myEnum;
  }

  public MyObject() {
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getCounter() {
    return counter;
  }
  public void setCounter(int counter) {
    this.counter = counter;
  }
  public MyNestedObject getMyNestedObject() {
    return this.myNestedObject;
  }

  public void setMyNestedObject(MyNestedObject myNestedObject) {
    this.myNestedObject = myNestedObject;
  }
  public MyEnum getMyEnum() {
    return this.myEnum;
  }
  public void setMyEnum(MyEnum myEnum) {
    this.myEnum = myEnum;
  }

}
