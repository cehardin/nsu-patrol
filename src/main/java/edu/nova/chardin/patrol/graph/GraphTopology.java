package edu.nova.chardin.patrol.graph;

import lombok.NonNull;

/**
 * The standard graphs for the experiment.
 * @author cehar
 */
public enum GraphTopology {

  A("graphTopologyA.csv"),
  B("graphTopologyB.csv"),
  C("graphTopologyC.csv");

  private final String fileName;

  private GraphTopology(@NonNull String fileName) {
    this.fileName = fileName;
  }

  /**
   * Get the file name for the graph.
   * @return The file name
   */
  public String getFileName() {
    return fileName;
  }
}
