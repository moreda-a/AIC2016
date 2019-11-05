package moreda;

import java.util.ArrayList;

public abstract class Strategy {
	private String name;
	protected GraphM graph;
	protected ArrayList<Order> orders;

	public Strategy(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void run(GraphM graph) {
		this.graph = graph;
		orders = new ArrayList<Order>();
		run();
	}

	public void addOrder(int src, int dst, int count) {
		orders.add(new Order(src, dst, count));
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public abstract void run();
}
