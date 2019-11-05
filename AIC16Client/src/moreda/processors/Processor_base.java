package moreda.processors;

import java.util.ArrayList;
import java.util.Map;

import moreda.Order;
import moreda.Processor;
import moreda.Strategy;

public class Processor_base extends Processor {

	public Processor_base() {
		super("pro1");
	}

	boolean firss = true;
	boolean firsss = true;

	@Override
	public void doTurn() {
		Strategy strategy;
		long le = 0;
		maxEfficient = Long.MIN_VALUE;
		opRun();
		boolean flag = false;
		for (Integer it : graph.isSafe[graph.myColor])
			if (it == 0)
				flag = true;
		if (flag && firss) {
			strategies.remove("str_new");
			firss = false;
		}
		// System.out.println("\nthis Turn" + graph.thisTurn);
		if (firsss) {
			bestStrategy = strategies.get("str_ff");
			bestStrategy.run(graph);
			makeNewGraph(bestStrategy.getOrders(), opOrders);
			le = performances.get("per1").evaluate(newGraph);
			firsss = false;
		} else {
			for (Map.Entry<String, Strategy> mapEntry : strategies.entrySet()) {
				opponent();
				strategy = mapEntry.getValue();
				strategy.run(graph);
				orders = strategy.getOrders();
				makeNewGraph(strategy.getOrders(), opOrders);
				le = performances.get("per1").evaluate(newGraph);
				if (maxEfficient <= le) {
					maxEfficient = le;
					bestStrategy = strategy;
				}
			}
		}
		orders = bestStrategy.getOrders();
		running();
	}

	@Override
	public void firstTurn() {
		// TODO Auto-generated method stub
	}

	@Override
	public ArrayList<Order> opRun() {
		// TODO Auto-generated method stub
		return null;
	}

	// 1-Opponent inner to outer
	private void opponent() {
		Integer flaga = 0;
		Integer is = -1;
		Integer minn1 = Integer.MAX_VALUE;
		Integer minn2 = Integer.MAX_VALUE;
		for (int i = 0; i < graph.size; ++i)
			graph.underAttack[i] = 0;
		for (Integer it : graph.nodeS[graph.enemyColor]) {
			flaga = 0;
			is = -1;
			minn1 = Integer.MAX_VALUE;
			minn2 = Integer.MAX_VALUE;
			if (graph.isOutter[graph.enemyColor][it] == 1) {
				for (Integer ie : graph.adjacencyList[it]) {
					if (graph.color[ie] == graph.myColor)
						if (graph.power[ie] < minn1) {
							minn1 = graph.power[ie];
							is = ie;
						} else if (graph.power[ie] < minn2) {
							minn2 = graph.power[ie];
						}
				}
				if (minn1 <= 10 && minn2 > 10) {
					graph.underAttack[is] = 1;
				}
				// TODO for better guess
			}
			if (graph.isOutter[graph.enemyColor][it] == 0) {
				for (Integer ie : graph.adjacencyList[it]) {
					if (graph.isOutter[graph.enemyColor][ie] == 1) {
						flaga += 1;
						is = ie;
					}
				}
				if (flaga == 1) {
					graph.power[is] += graph.power[it];
					graph.power[it] = 0;
					graph.xpower[it] = 0;
					if (graph.power[is] > 30)
						graph.xpower[is] = 2;
					else if (graph.power[is] > 10)
						graph.xpower[is] = 1;
					else
						graph.xpower[is] = 1;
				}
			}
		}

	}

}
