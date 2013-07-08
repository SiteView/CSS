package com.siteview.css.topo.test;

import java.lang.reflect.Method;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;


public class DirectedGraphDemo extends AbstractGraphDemo {

	/**
	 * Builds the graph, creates Draw2d figures for all graph components.
	 * 构建图,创建数据图表组件Draw2d所有
	 * @param graph
	 *            the graph to build
	 * @return the Figure representing the graph
	 */
	public static Figure buildGraph(DirectedGraph graph) {
		Figure contents = new Panel();
		contents.setBackgroundColor(ColorConstants.white);
		contents.setLayoutManager(new XYLayout());
		for (int i = 0; i < graph.nodes.size(); i++) {
			Node node = graph.nodes.getNode(i);
			buildNodeFigure(contents, node);
		}
		for (int i = 0; i < graph.edges.size(); i++) {
			Edge edge = graph.edges.getEdge(i);
			buildEdgeFigure(contents, edge);
		}
		return contents;
	}


	/**
	 * Runs this demo
	 * 
	 * @param args
	 *            command line args
	 */
	public static void main(String[] args) {
		new DirectedGraphDemo().run();
	}

	/**
	 * @see org.eclipse.graph.demo.GraphDemo#getGraphMethods()
	 */
	protected String[] getGraphMethods() {
		Method[] methods = GraphTests.class.getMethods();
		String[] methodNames = new String[methods.length];

		int nameIndex = 0;
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getReturnType().equals(DirectedGraph.class)) {
				methodNames[nameIndex] = methods[i].getName();
				nameIndex++;
			}
		}
		return methodNames;
	}

	/**
	 * @see org.eclipse.draw2d.examples.AbstractExample#getContents()
	 */
	protected IFigure getContents() {
		DirectedGraph graph = null;
		try {
			graph = (DirectedGraph) (GraphTests.class.getMethod(graphMethod,
					new Class[] { int.class }).invoke(null,
					new Object[] { new Integer(graphDirection) }));
		} catch (Exception e) {
			System.out.println("Could not build graph");
			e.printStackTrace();
		}
		Figure contents = buildGraph(graph);
		return contents;
	}
}
