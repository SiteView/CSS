package com.siteview.css.topo.test;

import java.util.Random;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;


public class GraphTests {
	static Random rand = new Random(90);

	/**
	 * @param nodes
	 * @param row
	 */
	private static void addNodes(NodeList nodes, Node[] row) {
		for (int i = 0; i < row.length; i++)
			if (row[i] != null)
				nodes.add(row[i]);
	}


	public static DirectedGraph fourLevelBinaryTree(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node row[], firstRow[];
		firstRow = new Node[2];
		firstRow[1] = new Node("root");
		addNodes(nodes, firstRow);

		row = joinRows(nodes, edges, firstRow, new int[] { 1, 1, 1, 2 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 1, 2, 2, 3, 2, 4 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 1, 2, 2, 3, 2, 4,
				3, 5, 3, 6, 4, 7, 4, 8 });

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);

		return graph;
	}

	/**
	 * @param nodes
	 * @param edges
	 * @param firstRow
	 * @param is
	 * @return
	 */
	private static Node[] joinRows(NodeList nodes, EdgeList edges,
			Node[] firstRow, int[] conns) {
		for (int i = 0; i < conns.length; i++) {
			System.out.println(conns[i]);
		}
		shuffleConnections(conns);
		Node secondRow[] = new Node[20];
		Node head, tail;
		for (int i = 0; i < conns.length; i += 2) {
			head = firstRow[conns[i]];
			tail = secondRow[conns[i + 1]];
			if (tail == null) {
				tail = secondRow[conns[i + 1]] = new Node("node" + conns[i + 1]);
				tail.width = 78;
			}
			edges.add(new Edge(head, tail));
		}
		addNodes(nodes, secondRow);
		return secondRow;
	}

	/**
	 * @param conns
	 */
	private static void shuffleConnections(int[] conns) {
		for (int i = 0; i < conns.length; i += 2) {
			int swap = (int) (rand.nextFloat() * conns.length) % conns.length
					/ 2;
			swap *= 2;
			int temp = conns[i];
			conns[i] = conns[swap];
			conns[swap] = temp;

			temp = conns[i + 1];
			conns[i + 1] = conns[swap + 1];
			conns[swap + 1] = temp;
		}
	}
}
