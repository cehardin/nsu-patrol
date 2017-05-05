package edu.nova.chardin.patrol;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class AbstractCsvWriter<T> {
  
  private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#######################.#######################");
  
  private static void writeLine(
          @NonNull final PrintWriter printWriter, 
          @NonNull final ImmutableList<String> elements) throws IOException {
    
    printWriter.println(String.join(",", elements));
  }
  
  private PrintWriter printWriter;

  AbstractCsvWriter(
          @NonNull final PrintWriter printWriter, 
          @NonNull final ImmutableList<String> fields) throws IOException {
    
    this.printWriter = printWriter;
    writeLine(printWriter, fields);
  }

  public final void write(@NonNull final T o) throws IOException {
    writeLine(printWriter, toFields(o));
  }
  
  public final void write(@NonNull final Iterable<T> iterable) throws IOException {
    for (final T o : iterable) {
      write(o);
    }
  }

  protected final String toString(final long value) {
    return NUMBER_FORMAT.format(value);
  }

  protected final String toString(final double value) {
    return NUMBER_FORMAT.format(value);
  }
  
  protected abstract ImmutableList<String> toFields(T o);
}
