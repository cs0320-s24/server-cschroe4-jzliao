package edu.brown.cs.student.mocks;

import edu.brown.cs.student.main.broadband.Broadband;
import edu.brown.cs.student.main.datasource.acs.ACSDatasource;
import edu.brown.cs.student.main.exceptions.EmptyResponseException;

import java.io.IOException;
import java.net.URISyntaxException;

public class MockACSDatasource implements ACSDatasource<Broadband> {
    private final Broadband constantData;

    public MockACSDatasource(Broadband constantData) {
        this.constantData = constantData;
    }

    @Override
    public Broadband sendRequest(String stateName, String countyName) throws URISyntaxException, IOException, EmptyResponseException, InterruptedException {
        return constantData;
    }
}
