package edu.brown.cs.student.main.broadband;

/**
 * This is a class that models an Activity received from the BoredAPI. It doesn't have a lot but
 * there are a few fields that you could filter on if you wanted!
 */
public class Broadband {
  private String name;
  private String percent;

  public Broadband(String name, String percent) {
    this.name = name;
    this.percent = percent;
  }

  @Override
  public String toString() {
    return "Percentage of households with broadband access in " + this.name + ": " + this.percent;
  }
}
