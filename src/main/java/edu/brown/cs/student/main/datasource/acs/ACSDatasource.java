package edu.brown.cs.student.main.datasource.acs;

import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface ACSDatasource<T> {
  public T sendRequest(String stateName, String countyName)
      throws URISyntaxException, IOException, EmptyResponseException, InterruptedException;
}
