package moreda;

public abstract class Performance {
	public String name;
	protected GraphM graph;

	public Performance(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public long evaluate(GraphM graph) {
		this.graph = graph;
		return evaluate();
	}

	public abstract long evaluate();
}
