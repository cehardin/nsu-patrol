package edu.nsu.chardin.patrol.ant.reporter;

import edu.nsu.chardin.patrol.ant.AntStepReporter;
import edu.nsu.chardin.patrol.graph.GraphData;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public abstract class AbstractAnimationReporter implements AntStepReporter<Double, Object> {

    private final int width;
    private final int height;
    private final int scale;
    private final double maxVertexValue;
    private final Color wallColor = new Color(0, 0, 0);
    private final Color[] vertexColors = new Color[256];

    public AbstractAnimationReporter(int width, int height, int scale, double maxVertexValue) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.maxVertexValue = maxVertexValue;

        for(int i=0; i <= 255; i++) {
            vertexColors[i] = new Color(0, i, 0);
        }
    }

    @Override
    public void report(int step, GraphData<Double, Object> graphData, SortedSet<Integer> locations) {
        final List<List<Boolean>> mask = graphData.getGraph().getMask();
        final double[][] vertexValues = graphData.getVextexValueGrid();
        final BufferedImage image = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_3BYTE_BGR);
        final Graphics2D g = image.createGraphics();
        
        g.scale(scale, scale);
        
        for(int y=0; y < height; y++) {
            for( int x=0; x < width; x++) {
                final Color color;
                
                if(mask.get(y).get(x)) {
                    color = wallColor;
                }
                else {
                    final double vertexValue = vertexValues[x][y];
                    final int colorOffset = (int)(255.0 * vertexValue / maxVertexValue);
                    color = vertexColors[colorOffset];
                }
                
                g.setColor(color);
                g.fillRect(x, y, 1, 1);
            }
        }
        
        g.dispose();
        
        report(step, image);
    }
    
    protected abstract void report(int step, RenderedImage image);
}
