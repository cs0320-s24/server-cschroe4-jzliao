package edu.brown.cs.student.main.datasource.acs;

public interface APIDatasource {
    public ACSRecord getPercent(int state, String county);
}
