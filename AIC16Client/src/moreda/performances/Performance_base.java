package moreda.performances;

import moreda.Performance;

public class Performance_base extends Performance {
	long[] ex;
	int[] cx;
	long mx;

	public Performance_base() {
		super("per1");
		ex = new long[6];
		cx = new int[6];
	}

	@Override
	public long evaluate() {
		mx = 0;
		ex[0] = incomeEdage(graph.myColor);
		cx[0] = 1000;
		ex[1] = incomeEdage(graph.enemyColor);
		cx[1] = 0;
		ex[2] = sigmaPower(graph.myColor);
		cx[2] = 30;
		ex[3] = sigmaPower(graph.enemyColor);
		cx[3] = -15;
		ex[4] = sigmaOuter(graph.myColor);
		cx[4] = 0;
		ex[5] = farest(graph.myColor);
		cx[5] = -600;
		for (int i = 0; i < 5; ++i)
			mx += ex[i] * cx[i];
		// System.out.println(mx + " i: " + ex[0] + " s: " + ex[2] + " o: " +
		// ex[3] + " f: " + ex[5]);
		return mx;
	}

	private long farest(int color) {
		long m = Long.MAX_VALUE;
		for (Integer it : graph.outter[color])
			m = Long.min(m, graph.psafeDistance[it]);
		return m;
	}

	private long sigmaOuter(int color) {
		long m = 0;
		for (Integer it : graph.outter[color])
			m += graph.power[it];
		return m;
	}

	private long sigmaPower(int color) {
		long m = 0;
		for (Integer it : graph.nodeS[color])
			m += graph.power[it];
		return m;
	}

	private long incomeEdage(int color) {
		long m = 0;
		for (Integer it : graph.nodeS[color])
			for (Integer ie : graph.adjacencyList[it])
				if (graph.color[ie] == color)
					m += graph.edageBonusConstant;
		return m / 2;
	}

}
