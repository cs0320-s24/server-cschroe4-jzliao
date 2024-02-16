package edu.brown.cs.student.main;

import static spark.Spark.after;

import edu.brown.cs.student.main.creators.TownCreator;
import edu.brown.cs.student.main.csvFunctions.Parser;
import edu.brown.cs.student.main.datasource.acs.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.acs.CacheBroadbandDatasource;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import edu.brown.cs.student.main.datasource.csv.LocalCSVSource;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.MalformedDataException;
import edu.brown.cs.student.main.handlers.*;
import edu.brown.cs.student.main.rowObjects.Town;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import spark.Spark;

public class Server {

  public Server(CSVDatasource csvState) {
    // make the server port
    int port = 1313;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    List<Town> parsedData = new ArrayList<>(); // todo maybe put in catch too so no need to reassign
    try {
      FileReader reader = new FileReader("./data/census/RICityTownIncome2017-2021.csv");
      Parser<Town> parser = new Parser<>(reader, new TownCreator());
      parsedData = parser.parseCSV();
    } catch (MalformedDataException | IOException | FactoryFailureException e) {
      System.out.println(e.getMessage());
    }

    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("towns", new SampleHandler(parsedData));
    Spark.get("loadcsv", new LoadHandler(csvState));
    Spark.get("viewcsv", new ViewHandler(csvState));
    Spark.get("searchcsv", new SearchHandler(csvState));
    Spark.get("broadband", new ACSHandler(new CacheBroadbandDatasource(new BroadbandDatasource(), 1)));

    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
  // TODO should we use the ACSSource instead of having the ACSHandler define the urls
  public static void main(String[] args) {
    Server server = new Server(new LocalCSVSource());
  }
}
