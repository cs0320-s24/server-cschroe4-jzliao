package edu.brown.cs.student.mocks;

import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class MockMultiACSDatasource implements ACSDatasource<Broadband> {

    private HashMap<String, Broadband> dataset;

    public MockMultiACSDatasource(HashMap<String,Broadband> broadbandMap){
        this.dataset = broadbandMap;
    }

    @Override
    public Broadband sendRequest(String stateName, String countyName) throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
        if (!this.dataset.containsKey(countyName+ "," + stateName)){
            throw new EmptyResponseException("Location not found");
        }
        return this.dataset.get(countyName+ "," + stateName);
    }


}
