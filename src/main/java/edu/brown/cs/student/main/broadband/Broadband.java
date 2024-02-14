package edu.brown.cs.student.main.broadband;

import java.util.Date;

/**
 * This is a class that models an Activity received from the BoredAPI. It doesn't have a lot but
 * there are a few fields that you could filter on if you wanted!
 */
public class Broadband {
  private String stateName;
  private String countyName;
  private double percentOfHouseholds;
  private Date date;
  public Broadband() {}

  @Override
  public String toString() {
    return "Percentage of households with broadband access in " + this.countyName + ", " + this.stateName + ": " + this.percentOfHouseholds;
  }
}
