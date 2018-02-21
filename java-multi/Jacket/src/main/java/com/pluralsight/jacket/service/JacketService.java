package com.pluralsight.jacket.service;

// Depends on class from Reository project
import com.pluralsight.repository.Repository;

public class JacketService {
  private int foo;
  private Repository repository;

  public JacketService() {
    this.repository = new Repository();
  }

  protected JacketService(int foo) {
    this.foo = foo;
  }

  public void doSomething() {

  }
 }
