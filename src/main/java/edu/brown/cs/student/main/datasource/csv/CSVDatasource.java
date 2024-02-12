package edu.brown.cs.student.main.datasource.csv;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;

import java.io.IOException;

public interface CSVDatasource {
    public CSVData getDataset();
    public void parseDataset(String filename) throws MalformedDataException, IOException, FactoryFailureException;
}
