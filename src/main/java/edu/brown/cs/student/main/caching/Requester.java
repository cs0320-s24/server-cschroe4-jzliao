package edu.brown.cs.student.main.caching;

import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface Requester {
  public String sendRequest(String stateNum, String countyName)
      throws URISyntaxException, IOException, EmptyResponseException, InterruptedException;
}
