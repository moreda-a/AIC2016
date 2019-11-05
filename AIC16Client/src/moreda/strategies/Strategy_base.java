package moreda.strategies;

import moreda.Strategy;
import moreda.intVector;

public class Strategy_base extends Strategy {

	public Strategy_base() {
		super("str_base");
	}

	public Strategy_base(String name) {
		super(name);
	}

	@Override
	public void run() {
		// TODO STRATEGY
		intVector myNodes = graph.nodeS[graph.myColor];
		for (Integer source : myNodes) {
			intVector neighbours = graph.adjacencyList[source];
			if (neighbours.size() > 0) {
				Integer destination = neighbours.get((int) (neighbours.size() * Math.random()));
				addOrder(source, destination, graph.power[source] / 2);
			}
		}
	}

}
