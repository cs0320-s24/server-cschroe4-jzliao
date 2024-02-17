package edu.brown.cs.student.main.datasource.csv;

import edu.brown.cs.student.main.exceptions.DataNotLoadedException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import java.io.IOException;

/** Interface for a datasource taken in by csv handlers */
public interface CSVDatasource {
  /**
   * @return the dataset stored
   * @throws DataNotLoadedException
   */
  public CSVData getDataset() throws DataNotLoadedException;

  /**
   * parses dataset stored in file passed in
   *
   * @param filename
   * @param hasHeader
   * @throws MalformedDataException
   * @throws IOException
   * @throws FactoryFailureException
   */
  public void parseDataset(String filename, boolean hasHeader)
      throws MalformedDataException, IOException, FactoryFailureException;
}
