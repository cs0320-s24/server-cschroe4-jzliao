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
        /*TODO:
           -instantiate searcher?
            - parameters: copy logic from main
            - q: if dataset is a CSVData object how do we get it as a list to search?
            - also the current searcher makes its own parser so how should we bypass that or change the structure?
            - return a record with a response map containing each of the matching rows.
         */

        return null;
    }
}
