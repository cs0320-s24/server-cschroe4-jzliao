package edu.brown.cs.student.main.datasource.csv;

import edu.brown.cs.student.main.exceptions.DataNotLoadedException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import java.io.IOException;

public interface CSVDatasource {
  public CSVData getDataset() throws DataNotLoadedException;

  public void parseDataset(String filename, boolean hasHeader)
      throws MalformedDataException, IOException, FactoryFailureException;
}
