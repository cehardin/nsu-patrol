/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nova.chardin.patrol.graph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author cehar
 */
public class PatrolGraphGeneratorClass {

    @Test
    @Ignore
    public void createGraph() throws Exception {
        final Injector injector = Guice.createInjector(new GraphModule());
        final Provider<PatrolGraph> graphProvider = injector.getProvider(PatrolGraph.class);
        final ExecutorService executorService = Executors.newWorkStealingPool();
        final List<Future<?>> futures = new ArrayList<>(1024 * 1024);

        LongStream.range(0, 10).forEach(seed -> {
            IntStream.range(5, 20).forEach(numVertices -> {
                IntStream.range(numVertices, numVertices * 10).forEach(widthLength -> {
                    for(double degree = 1; degree < 10; degree += 0.5) {
                        final double d = degree;
                        futures.add(executorService.submit(() -> {
                            final PatrolGraphGenerator generator = new PatrolGraphGenerator(
                                    seed,
                                    injector.getProvider(PatrolGraph.class),
                                    widthLength,
                                    numVertices,
                                    d);

                            final PatrolGraph graph = generator.create();

                            assertEquals("Number of vertices was incorrrect", numVertices, graph.vertexSet().size());
                            assertEquals("Degree was not in range", d, graph.getAverageDegree(), 2.0);
                        }));
                    }
                });
            });
        });

        for (final Future<?> future : futures) {
            future.get();
        }
    }
}
