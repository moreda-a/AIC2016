package moreda;

public class Order {
	private int src;
	private int dst;
	private int cnt;

	public Order(int src, int dst, int count) {
		this.src = src;
		this.dst = dst;
		this.cnt = count;
	}

	public int getSource() {
		return src;
	}

	public int getDestination() {
		return dst;
	}

	public int getCount() {
		return cnt;
	}

}
