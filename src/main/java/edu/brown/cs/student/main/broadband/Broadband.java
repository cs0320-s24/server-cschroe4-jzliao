package edu.brown.cs.student.main.broadband;

/**
 * This class represents broadband data for a certain location. It also stores the date that the
 * information was fetched
 */
public class Broadband {
  private String name;
  private String percent;

  private String dateFetched;

  /**
   * Constructor for the class. Stores the name of the location (county, state), the broadband
   * access percentage and the date the information was fetched as a String
   *
   * @param name
   * @param percent
   * @param date
   */
  public Broadband(String name, String percent, String date) {
    this.name = name;
    this.percent = percent;
    this.dateFetched = date;
  }

  /**
   * @return name of the location of the information
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return all of the stored information formatted as a sentence and returned as a String
   */
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

  /**
   * .equals() method. Makes sure the passed in object is not null and is a Broadband, then compares
   * the results of their calls to toString()
   *
   * @param obj object comparing itself to
   * @return boolean on whether the objects are equal or not
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }

    return this.toString().equals(obj.toString());
  }
}
