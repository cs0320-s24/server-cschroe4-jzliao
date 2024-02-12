package edu.brown.cs.student.main.handlers;

import edu.brown.cs.student.main.datasource.csv.CSVData;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class ViewHandler implements Route {

    private CSVDatasource state;
    public ViewHandler(CSVDatasource state){
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        CSVData dataset = this.state.getDataset();
        // put each row into response map?
        return null;
    }
}
