package edu.brown.cs.student.main.datasource.csv;

import java.util.List;

/**
 * Record that represents CSVData
 *
 * @param dataset the parsed dataset as a list of rows
 * @param hasHeader a boolean indicating if there is a header for the csv
 */
public record CSVData(List<List<String>> dataset, boolean hasHeader) {}
