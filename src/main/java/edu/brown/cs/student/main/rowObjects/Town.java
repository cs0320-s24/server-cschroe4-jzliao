package edu.brown.cs.student.main.rowObjects;

public class Town {

  private String name;
  private String medianHouseholdIncome;
  private String medianFamilyIncome;
  private String perCapitaIncome;

  /**
   * Sets the fields to what is inputted
   *
   * @param name the town's name
   * @param medianHouseholdIncome is income for households
   * @param medianFamilyIncome the town's medianFamilyIncome
   * @param perCapitcaIncome town's perCapitcaIncome
   */
  public Town(
      String name,
      String medianHouseholdIncome,
      String medianFamilyIncome,
      String perCapitcaIncome) {
    this.name = name;
    this.medianHouseholdIncome = medianHouseholdIncome;
    this.medianFamilyIncome = medianFamilyIncome;
    this.perCapitaIncome = perCapitcaIncome;
  }

  /**
   * Gets the star ID
   *
   * @return the starID string
   */
  public String getName() {
    return this.name;
  }

  public String getPerCapitaIncome() {
    return this.perCapitaIncome;
  }
}
