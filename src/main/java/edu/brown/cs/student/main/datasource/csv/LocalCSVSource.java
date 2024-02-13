package edu.brown.cs.student.main.datasource.csv;

import edu.brown.cs.student.main.csvFunctions.Parser;
import edu.brown.cs.student.main.creators.ListCreator;
import edu.brown.cs.student.main.exceptions.DataNotLoadedException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class LocalCSVSource implements CSVDatasource{
    private CSVData dataset;
    private boolean parsed;
    public LocalCSVSource(){
        this.parsed = false;
    }
    @Override
    public CSVData getDataset() throws DataNotLoadedException {
        if(parsed) {
            return dataset; //todo make this safer?
        }
        throw new DataNotLoadedException("Load data successfully before requesting dataset");
    }

    public void parseDataset(String filename) throws MalformedDataException, IOException, FactoryFailureException {
        Reader reader = new FileReader(filename); // could throw file not found exception
        Parser<List<String>> parser = new Parser<>(reader, new ListCreator());
        this.dataset = new CSVData(parser.parseCSV());
        this.parsed = true;
    }


}
