package edu.brown.cs.student.main.handlers;

import edu.brown.cs.student.main.datasource.csv.CSVData;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

    private CSVDatasource state;
    public SearchHandler(CSVDatasource state){
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        CSVData dataset = this.state.getDataset();
        // instantiate searcher?

        return null;
    }
}
