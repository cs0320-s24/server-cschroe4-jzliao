package edu.brown.cs.student.main.datasource.acs;

import edu.brown.cs.student.main.exceptions.EmptyResponseException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Interface for datasources for the ACSHandler
 *
 * @param <T> type of data requested
 */
public interface ACSDatasource<T> {
  /**
   * Sends a request for data of type T based on the passed on parameters
   *
   * @param stateName
   * @param countyName
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws EmptyResponseException
   * @throws InterruptedException
   */
  public T sendRequest(String stateName, String countyName)
      throws URISyntaxException, IOException, EmptyResponseException, InterruptedException;
}
