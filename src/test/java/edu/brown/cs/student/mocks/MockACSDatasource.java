package edu.brown.cs.student.mocks;

import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import edu.brown.cs.student.main.handlers.ACSHandler;

import java.io.IOException;
import java.net.URISyntaxException;

public class MockACSDatasource implements ACSDatasource<Broadband> {
  private final Broadband constantData;

  public MockACSDatasource(Broadband constantData) {
    this.constantData = constantData;
  }

  @Override
  public Broadband sendRequest(String stateName, String countyName)
      throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
    String name = countyName + ", "+stateName;
    if(!name.equals(this.constantData.getName())){
      throw new EmptyResponseException("County or state data not found");
    }
    return constantData;
  }
}
