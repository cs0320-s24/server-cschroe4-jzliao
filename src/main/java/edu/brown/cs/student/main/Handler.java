package edu.brown.cs.student.main;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.rowObjects.Town;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler implements Route {

    private List<Town> riData;
    public Handler(List<Town> parsedData) {
        this.riData = parsedData;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String townName = request.queryParams("townName");
        Map<String, Object> responseMap = new HashMap<>();
        // Iterate through the soups in the menu and return the first one
        for (Town town : this.riData) {
            if(town.getName().equalsIgnoreCase(townName)){
                responseMap.put(town.getName(), town);
                responseMap.put("Per Capita income", town.getPerCapitaIncome());
                return new TownSuccessResponse(responseMap).serialize();
            }
        }
        return null;
    }

    /** Response object to send, containing a soup with certain ingredients in it */
    public record TownSuccessResponse(String response_type, Map<String, Object> responseMap) {
        public TownSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            try {
                // Initialize Moshi which takes in this class and returns it as JSON!
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<TownSuccessResponse> adapter = moshi.adapter(TownSuccessResponse.class);
                return adapter.toJson(this);
            } catch (Exception e) {
                // For debugging purposes, show in the console _why_ this fails
                // Otherwise we'll just get an error 500 from the API in integration
                // testing.
                e.printStackTrace();
                throw e;
            }
        }
    }

    /** Response object to send if someone requested soup from an empty Menu */
    public record NoTownsFailureResponse(String response_type) {
        public NoTownsFailureResponse() {
            this("error");
        }

        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(NoTownsFailureResponse.class).toJson(this);
        }
    }
}
