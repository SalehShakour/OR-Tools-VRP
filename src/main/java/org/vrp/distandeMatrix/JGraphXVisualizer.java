package org.vrp.distandeMatrix;

import com.mxgraph.swing.mxGraphComponent;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.mxgraph.view.mxGraph;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.awt.event.*;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JGraphXVisualizer extends JFrame {

    static long[][] DISTANCE_MATRIX;
    @NonFinal
    mxGraphComponent graphComponent;
    @NonFinal
    Point initialClick;

    public JGraphXVisualizer(long[][] distanceMatrix) {
        super("Graph Visualization");
        DISTANCE_MATRIX = distanceMatrix;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameSize = (int) (screenSize.width * 0.35);
        setSize(frameSize, frameSize);
        int fixY = (screenSize.height - frameSize) / 2;
        int fixX = (screenSize.width - frameSize) / 2;
        setLocation(fixX, fixY);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        enableDragging();
        addResizeListener();

        drawGraph();
        setVisible(true);
    }

    private void enableDragging() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
    }

    private void addResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateGraph();
            }
        });
    }

    private void drawGraph() {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        Map<Integer, Object> nodes = new HashMap<>();
        graph.getModel().beginUpdate();
        try {
            int numNodes = DISTANCE_MATRIX.length;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = Math.min(getWidth(), getHeight()) / 3;

            for (int i = 0; i < numNodes; i++) {
                double angle = 2 * Math.PI * i / numNodes;
                int x = centerX + (int) (radius * Math.cos(angle)) - 30;
                int y = centerY + (int) (radius * Math.sin(angle)) - 30;
                Object vertex = graph.insertVertex(
                        parent, null, "Node " + i, x, y, 60, 60
                        , "fillColor=lightblue;shape=ellipse;"
                );
                nodes.put(i, vertex);
            }

            for (int i = 0; i < numNodes; i++) {
                for (int j = i + 1; j < numNodes; j++) {
                    if (DISTANCE_MATRIX[i][j] > 0) {
                        graph.insertEdge(parent, null, String.valueOf(DISTANCE_MATRIX[i][j]), nodes.get(i),
                                nodes.get(j), "strokeColor=orange;fontColor=green;endArrow=none;"
                        );
                    }
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        if (graphComponent != null) {
            getContentPane().remove(graphComponent);
        }

        graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
        revalidate();
        repaint();
    }

    private void updateGraph() {
        drawGraph();
    }
}
