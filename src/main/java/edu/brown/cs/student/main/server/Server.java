package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.acs.CacheBroadbandDatasource;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import edu.brown.cs.student.main.datasource.csv.LocalCSVSource;
import edu.brown.cs.student.main.handlers.*;
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

    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("loadcsv", new LoadHandler(csvState));
    Spark.get("viewcsv", new ViewHandler(csvState));
    Spark.get("searchcsv", new SearchHandler(csvState));
    Spark.get(
        "broadband",
        new ACSHandler<Broadband>(
            new CacheBroadbandDatasource(new BroadbandDatasource(), 60), "broadband"));

    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
  // TODO should we use the ACSSource instead of having the ACSHandler define the urls
  public static void main(String[] args) {
    System.out.println("Server started");
    Server server = new Server(new LocalCSVSource());
  }
}
