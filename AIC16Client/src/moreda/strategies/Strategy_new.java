package moreda.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import moreda.Order;
import moreda.Strategy;
import moreda.intVector;

public class Strategy_new extends Strategy {
	private Strategy str = new Strategy_ff("in new Strategy");

	public Strategy_new() {
		super("str_new");
	}

	public Strategy_new(String name) {
		super(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap sortByValues(HashMap map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	@SuppressWarnings("unchecked")
	private void matchingNeighboursGraph(ArrayList<Integer> myNodes) {
		HashMap<Integer, Integer> neighbourVals = new HashMap<>();
		for (Integer node : myNodes)
			for (Integer neighbour : graph.adjacencyList[node])
				if (graph.color[neighbour] != graph.myColor) {
					/*
					 * int val = 100 * graph.psafeDistance[neighbour];
					 * ArrayList<Order> tmp = new ArrayList<>(); tmp.add(new
					 * Order(node, neighbour, graph.power[node])); val +=
					 * performance.evaluate(simluator.runOn(graph, tmp, null)) /
					 * 10; System.out.println(val + "  " + neighbour);
					 * neighbourVals.put(neighbour, val);
					 */

					neighbourVals.put(neighbour, 100 - graph.safeDistance[graph.myColor][neighbour]);
				}
		neighbourVals = sortByValues(neighbourVals);

		boolean[] mark = new boolean[graph.size];
		Arrays.fill(mark, false);

		for (Integer neighbour : neighbourVals.keySet()) {
			int minDegree = Integer.MAX_VALUE;
			int v = -1;
			int d;
			for (Integer node : graph.adjacencyList[neighbour])
				if (graph.color[node] == graph.myColor && !mark[node])
					if ((d = graph.adjacencyList[node].size()) < minDegree) {
						minDegree = d;
						v = node;
					}
			if (v >= 0) {
				mark[v] = true;
				addOrder(v, neighbour, graph.power[v]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	void moveInnerNodes() {
		ArrayList<Integer> outers = new ArrayList<>();

		for (Order o : this.orders)
			outers.add(o.getSource());

		boolean[] mark = new boolean[graph.size];
		Arrays.fill(mark, false);
		for (Integer node : outers)
			mark[node] = true;

		int count = graph.nodeS[graph.myColor].size() - outers.size();
		while (count > 0) {
			if (outers.size() == 0)
				break;
			HashMap<Integer, Integer> outersVals = new HashMap<>();
			for (Integer node : outers)
				for (Integer neighbour : graph.adjacencyList[node])
					if (graph.color[neighbour] == graph.myColor) {
						/*
						 * int val = 100 * graph.psafeDistance[neighbour];
						 * ArrayList<Order> tmp = new ArrayList<>(); tmp.add(new
						 * Order(node, neighbour, graph.power[node])); val +=
						 * performance.evaluate(simluator.runOn(graph, tmp,
						 * null)) / 10; System.out.println(val + "  " +
						 * neighbour); neighbourVals.put(neighbour, val);
						 */

						// outersVals.put(node, 100 -
						// graph.safeDistance[graph.myColor][node]);
						outersVals.put(node, 100000 - graph.power[node]);
					}
			outersVals = sortByValues(outersVals);
			outers.clear();

			for (Integer node : outersVals.keySet()) {
				int minDegree = Integer.MAX_VALUE;
				int v = -1;
				int d;
				for (Integer neighbour : graph.adjacencyList[node])
					if (graph.color[neighbour] == graph.myColor && !mark[neighbour])
						if ((d = graph.adjacencyList[neighbour].size()) < minDegree) {
							minDegree = d;
							v = neighbour;
						}
				if (v >= 0) {
					mark[v] = true;
					count--;
					addOrder(v, node, graph.power[v]);
					outers.add(v);
				}
			}
		}
	}

	@Override
	public void run() {
		// TODO STRATEGY
		intVector myNodes = graph.nodeS[graph.myColor];
		/*
		 * for (Integer source : myNodes) { intVector neighbours =
		 * graph.adjacencyList[source]; if (neighbours.size() > 0) { Integer
		 * destination = neighbours.get((int) (neighbours.size() *
		 * Math.random())); addOrder(source, destination, graph.power[source] /
		 * 2); } }
		 */
		boolean flag = false;
		for (Integer it : graph.isSafe[graph.myColor])
			if (it == 0)
				flag = true;
		if (flag) {
			System.out.println("PlanChanged");
			str.run(graph);
			orders = str.getOrders();
		} else {
			matchingNeighboursGraph(myNodes);
			moveInnerNodes();
		}
	}
}
