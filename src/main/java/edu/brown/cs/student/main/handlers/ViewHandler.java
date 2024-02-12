package edu.brown.cs.student.main.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.csv.CSVData;
import edu.brown.cs.student.main.datasource.csv.CSVDatasource;
import edu.brown.cs.student.main.exceptions.DataNotLoadedException;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewHandler implements Route {

    private CSVDatasource state;
    public ViewHandler(CSVDatasource state){
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try{
            CSVData dataset = this.state.getDataset();
            Map<String, Object> responseMap = new HashMap<>();
            // put each row into response map?
            responseMap.put("CSV data", dataset);
            return new ViewSuccessResponse(responseMap).serialize();
        } catch (DataNotLoadedException e){
            return new ViewFailureResponse(e.getMessage()).serialize();
        }
    }

    public record ViewSuccessResponse(String response_type, Map<String, Object> responseMap) {
        public ViewSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            try {
                // Initialize Moshi which takes in this class and returns it as JSON!
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<ViewHandler.ViewSuccessResponse> adapter =
                        moshi.adapter(ViewHandler.ViewSuccessResponse.class);
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

    /** Response object to send if someone gave a CSV that couldn't be parsed */
    public record ViewFailureResponse(String response_type, String error_message) {
        public ViewFailureResponse(String errorMessage) {
            this("error", errorMessage);
        }

        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(ViewHandler.ViewFailureResponse.class).toJson(this);
        }
    }
}
