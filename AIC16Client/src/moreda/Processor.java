package moreda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import client.World;
import moreda.performances.Performance_base;
import moreda.strategies.Strategy_base;
import moreda.strategies.Strategy_ff;
import moreda.strategies.Strategy_new;

public abstract class Processor {
	private String name;
	private boolean firstTime = true;
	protected GraphM graph;
	protected World world;
	protected GraphM newGraph;
	protected Map<String, Performance> performances;
	protected Map<String, Strategy> strategies;
	protected long maxEfficient = 0;
	protected Strategy bestStrategy;
	protected ArrayList<Order> orders;
	protected ArrayList<Order> opOrders;
	private Simulator simulator;

	public Processor(String name) {
		this.name = name;
		performances = new HashMap<String, Performance>();
		strategies = new HashMap<String, Strategy>();

		simulator = new Simulator();
		// add any Performance HERE:
		/*
		 * per1 as first performance
		 */
		Performance performance = new Performance_base();
		performances.put(performance.getName(), performance);

		// add any Strategies HERE:
		/*
		 * str1 as first strategy
		 */
		Strategy strategy = new Strategy_base();
		// strategies.put(strategy.getName(), strategy);
		strategy = new Strategy_ff();
		strategies.put(strategy.getName(), strategy);
		strategy = new Strategy_new();
		strategies.put(strategy.getName(), strategy);
		// strategy = new Strategy_base("strb2");
		// strategies.put(strategy.getName(), strategy);
		// strategy = new Strategy_base("strb3");
		// strategies.put(strategy.getName(), strategy);
		// strategy = new Strategy_base("strb4");
		// strategies.put(strategy.getName(), strategy);
		// strategy = new Strategy_base("strb5");
		// strategies.put(strategy.getName(), strategy);
	}

	public String getName() {
		return name;
	}

	public void doTurn(World world) {
		this.world = world;
		if (firstTime) {
			graph = new GraphM(world);
			firstTurn();
			firstTime = false;

		} else
			graph.updateGraph(world);
		doTurn();
	}

	public GraphM getNewGraph() {
		return newGraph;
	}

	public void running() {
		for (Order order : orders)
			world.moveArmy(order.getSource(), order.getDestination(), order.getCount());
	}

	public void makeNewGraph(ArrayList<Order> orders, ArrayList<Order> opOrders) {
		newGraph = simulator.runOn(graph, orders, opOrders);
	}

	abstract protected void doTurn();

	abstract protected void firstTurn();

	abstract public ArrayList<Order> opRun();

}