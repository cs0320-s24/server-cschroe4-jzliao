package edu.brown.cs.student.main.broadband;

/**
 * This is a class that models an Activity received from the BoredAPI. It doesn't have a lot but
 * there are a few fields that you could filter on if you wanted!
 */
public class Broadband {
  private String name;
  private String percent;

  private String dateFetched;

  public Broadband(String name, String percent, String date) {
    this.name = name;
    this.percent = percent;
    this.dateFetched = date;
  }

  public String getName(){
    return this.name;
  }

  @Override
  public String toString() {
    return "As of "
        + this.dateFetched
        + ", the percentage of households with broadband access in "
        + this.name
        + " was "
        + this.percent
        + "%";
  }

  @Override
  public boolean equals(Object obj){
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }

    return this.toString().equals(obj.toString());
  }
}
