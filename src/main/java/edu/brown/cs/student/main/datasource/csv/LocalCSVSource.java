package edu.brown.cs.student.main.datasource.csv;

import edu.brown.cs.student.main.creators.ListCreator;
import edu.brown.cs.student.main.csvFunctions.Parser;
import edu.brown.cs.student.main.exceptions.DataNotLoadedException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/** Datasource for csv-related handlers. Stores parses csv files and stores the parsed data */
public class LocalCSVSource implements CSVDatasource {
  private CSVData dataset;
  private boolean parsed;

  /** Constructor for class. Sets the parsed boolean as false */
  public LocalCSVSource() {
    this.parsed = false;
  }

  /**
   * Returns the stored dataset if it is parsed
   *
   * @return
   * @throws DataNotLoadedException
   */
  @Override
  public CSVData getDataset() throws DataNotLoadedException {
    if (parsed) {
      return dataset;
    }
    throw new DataNotLoadedException("Load data successfully before requesting dataset");
  }

  /**
   * Parses a csv stored in the file passed in by creating a Parser.
   *
   * @param filename
   * @param hasHeader boolean indicating if the file has a header
   * @throws MalformedDataException
   * @throws IOException
   * @throws FactoryFailureException
   */
  public void parseDataset(String filename, boolean hasHeader)
      throws MalformedDataException, IOException, FactoryFailureException {
    Reader reader = new FileReader(filename); // could throw file not found exception
    Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
    this.dataset = new CSVData(parser.parseCSV(), hasHeader);
    this.parsed = true;
  }
}
