package edu.nova.chardin.patrol.graph.loader;

import com.google.common.base.Preconditions;

import java.util.Objects;

public enum XmlGraph {
  
  A("map_a.xml"), 
  B("map_b.xml"),
  Circle("map_circle.xml"),
  Corridor("map_corridor.xml"),
  Grid("map_grid.xml"),
  Islands("map_islands.xml");
  
  private final String fileName;
  
  private XmlGraph(final String fileName) {
    this.fileName = Objects.requireNonNull(fileName, "File name must not be null").trim();
    
    Preconditions.checkArgument(!this.fileName.isEmpty(), "File name must not be empty");
  }
  
  public String fileName() {
    return fileName;
  }
}
