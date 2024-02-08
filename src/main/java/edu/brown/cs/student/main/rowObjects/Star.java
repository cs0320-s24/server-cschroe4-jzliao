package edu.brown.cs.student.main.rowObjects;

/** Star is an example class of an object a developer might want to turn a row into */
public class Star {
  private String starID;
  private String properName;
  private String x;
  private String y;
  private String z;

  /**
   * Sets the fields to what is inputted
   *
   * @param starID the star's ID
   * @param properName the star's proper name
   * @param x the star's x coordinate
   * @param y the star's y coordinate
   * @param z the star's z coordinate
   */
  public Star(String starID, String properName, String x, String y, String z) {
    this.starID = starID;
    this.properName = properName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /** Prints the fields to terminal neatly */
  public void printAttributes() {
    System.out.println("starID: " + this.starID);
    System.out.println("properName: " + this.properName);
    System.out.println("x: " + this.x);
    System.out.println("y: " + this.y);
    System.out.println("z: " + this.z);
  }

  /**
   * Gets the star ID
   *
   * @return the starID string
   */
  public String getStarID() {
    return this.starID;
  }
}
