package moreda.strategies;

import moreda.Strategy;

public class Strategy_ff extends Strategy {
	private final int myConst1 = 6;

	private int colori[];
	private int attacked[];
	private int sup[];

	public Strategy_ff() {
		super("str_ff");
	}

	public Strategy_ff(String name) {
		super(name);
	}

	@Override
	public void run() {
		colori = new int[graph.size];
		attacked = new int[graph.size];
		sup = new int[graph.size];
		for (int i = 0; i < graph.size; ++i) {
			attacked[i] = 0;
			sup[i] = 0;
			colori[i] = graph.color[i];
		}
		// Inner nodes and safe outer nodes
		for (Integer it : graph.nodeS[graph.myColor]) {
			// Inner nodes
			if (graph.isOutter[graph.myColor][it] == 0 && (graph.psafeDistance[it] != Integer.MAX_VALUE / 2
					|| graph.xsafeDistance[graph.enemyColor][it] != Integer.MAX_VALUE / 2))
				innerNode(it);
			// Safe outer nodes
			else if (graph.isOutter[graph.myColor][it] == 1 && graph.isSafe[graph.myColor][it] == 1)
				safeOuterNode(it);
		}
		// Outer Nodes in WAR
		for (Integer it : graph.outter[graph.myColor]) {
			if (graph.isSafe[graph.myColor][it] == 0) {
				// supported nodes
				if (attacked[it] == 1)
					supportedWarNode(it);
				// unsupported nodes
				else
					unsupportedWarNode(it);
			}
		}
	}

	// inner nodes
	private void innerNode(Integer it) {
		Integer minn = Integer.MAX_VALUE / 2 - 1;
		Integer ir = -1;
		Integer flaga = 0;
		// Inner nodes
		if (graph.psafeDistance[it] < graph.xsafeDistance[graph.enemyColor][it]) {
			for (Integer ie : graph.adjacencyList[it]) {
				{
					if (graph.underAttack[ie] == 1) {
						if (flaga == 0 || sup[ie] + graph.power[ie] < sup[ir] + graph.power[ir]) {
							minn = graph.psafeDistance[ie];
							ir = ie;
							flaga = 1;
						}
					} else if (flaga == 0 && graph.psafeDistance[ie] < minn) {
						minn = graph.psafeDistance[ie];
						ir = ie;
					} else if (graph.psafeDistance[ie] == minn) {
						if (graph.xsafeDistance[graph.enemyColor][ie] < graph.xsafeDistance[graph.enemyColor][ir])
							ir = ie;
						else if (graph.xsafeDistance[graph.enemyColor][ie] == graph.xsafeDistance[graph.enemyColor][ir]) {
							if (graph.power[graph.xnear[graph.enemyColor][ie]] > graph.power[graph.xnear[graph.enemyColor][ir]])
								ir = ie;
							if (graph.power[graph.xnear[graph.enemyColor][ie]] == graph.power[graph.xnear[graph.enemyColor][ir]])
								if (graph.power[ie] < graph.power[ir])
									ir = ie;
						}
					}
				}
			}
			addOrder(it, ir, graph.power[it]);
			attacked[ir] = 1;
			sup[ir] += graph.power[it];
		} else {
			for (Integer ie : graph.adjacencyList[it]) {
				if (graph.xsafeDistance[graph.enemyColor][ie] < minn) {
					minn = graph.xsafeDistance[graph.enemyColor][ie];
					ir = ie;
				} else if (graph.xsafeDistance[graph.enemyColor][ie] == graph.xsafeDistance[graph.enemyColor][ir]) {
					if (graph.power[graph.xnear[graph.enemyColor][ie]] > graph.power[graph.xnear[graph.enemyColor][ir]])
						ir = ie;
					if (graph.power[graph.xnear[graph.enemyColor][ie]] == graph.power[graph.xnear[graph.enemyColor][ir]])
						if (graph.power[ie] < graph.power[ir])
							ir = ie;
				}
			}
			addOrder(it, ir, graph.power[it]);
			attacked[ir] = 1;
			sup[ir] += graph.power[it];
		}
	}

	private void safeOuterNode(Integer it) {
		Integer minn = Integer.MAX_VALUE / 2 - 1;
		Integer ir = -1;
		Integer maxx = -1;
		Integer iw = -1;

		for (Integer ie : graph.border)
			if (graph.distance[it][ie] != Integer.MAX_VALUE / 2 && maxx < graph.distance[it][ie]) {
				maxx = graph.distance[it][ie];
				ir = ie;
			}
		for (Integer ie : graph.adjacencyList[it])
			if (colori[ie] == -1) {
				if (ir == -1)
					iw = ie;
				else if (graph.distance[ir][ie] != Integer.MAX_VALUE / 2 && graph.distance[ir][ie] <= minn) {
					if (graph.distance[ir][ie] == minn) {
						if (graph.xsafeDistance[graph.enemyColor][ie] < graph.xsafeDistance[graph.enemyColor][iw]) {
							minn = graph.distance[ir][ie];
							iw = ie;
						} else if (graph.xsafeDistance[graph.enemyColor][ie] == graph.xsafeDistance[graph.enemyColor][iw])
							if (graph.adjacencyList[iw].size() < graph.adjacencyList[ie].size()) {
								minn = graph.distance[ir][ie];
								iw = ie;
							}
					} else {
						minn = graph.distance[ir][ie];
						iw = ie;
					}
				}
			}
		// if near empty node was captured then help other
		if (iw == -1) {
			if (graph.psafeDistance[it] != Integer.MAX_VALUE / 2
					|| graph.xsafeDistance[graph.enemyColor][it] != Integer.MAX_VALUE / 2)
				if (graph.psafeDistance[it] < graph.xsafeDistance[graph.enemyColor][it]) {
					addOrder(it, graph.pparent[it], graph.power[it]);
					iw = graph.pparent[it];
				} else {
					addOrder(it, graph.xparent[graph.enemyColor][it], graph.power[it]);
					iw = graph.xparent[graph.enemyColor][it];
				}
		} else {
			if (graph.safeDistance[graph.enemyColor][iw] == Integer.MAX_VALUE / 2) {
				addOrder(it, iw, 1);
				sup[iw] -= graph.power[it] - 1;
			} else {
				if (graph.power[it] > 35 && graph.xsafeDistance[graph.enemyColor][it] <= 3) {
					if (graph.xsafeDistance[graph.enemyColor][iw] >= graph.xsafeDistance[graph.enemyColor][it]) {
						if (graph.power[it] > 75)
							addOrder(it, iw, graph.power[it] - 15);
						else if (graph.power[it] > 65)
							addOrder(it, iw, graph.power[it] - 13);
						else if (graph.power[it] > 55)
							addOrder(it, iw, graph.power[it] - 11);
						else if (graph.power[it] > 45)
							addOrder(it, iw, graph.power[it] - 9);
						else if (graph.power[it] > 35)
							addOrder(it, iw, graph.power[it] - 7);
						else
							addOrder(it, iw, graph.power[it] - 5);
					} else {
						if (graph.xsafeDistance[graph.enemyColor][it] <= 2)
							addOrder(it, iw, graph.power[it] - 5);
						else
							addOrder(it, iw, graph.power[it] - 2);
					}
				} else
					addOrder(it, iw, graph.power[it]);
			}
		}
		attacked[iw] = 1;
		colori[iw] = graph.myColor;
		sup[iw] += graph.power[it];
	}

	private void supportedWarNode(Integer it) {
		Integer ir = -1;
		Integer iw = -1;
		Integer maxx = -1;
		Integer flaga = 0;
		Integer flagb = 0;
		int pp, qq;
		Integer minn = Integer.MAX_VALUE / 2 - 1;
		for (Integer ie : graph.adjacencyList[it]) {
			if (colori[ie] != graph.myColor && maxx < graph.power[ie])
				if (graph.power[it] >= graph.power[ie]) {
					maxx = graph.power[ie];
					ir = ie;
				} else if (graph.power[ie] < minn) {
					minn = graph.power[ie];
					iw = ie;
				}
		}
		// ??
		if (ir == -1 && iw == -1) {
			for (Integer ie : graph.adjacencyList[it]) {
				if (graph.color[ie] != graph.myColor && maxx < graph.power[ie])
					if (graph.power[it] >= graph.power[ie]) {
						maxx = graph.power[ie];
						ir = ie;
					} else if (graph.power[ie] < minn) {
						minn = graph.power[ie];
						iw = ie;
					}
			}
		}
		if (ir != -1) {
			for (Integer is : graph.adjacencyList[ir])
				if (colori[is] == graph.enemyColor)
					flagb = 1;
			if (graph.color[ir] == -1 && flagb == 0) {
				if (graph.safeDistance[graph.enemyColor][ir] == Integer.MAX_VALUE / 2)
					addOrder(it, ir, 1);
				else
					addOrder(it, ir, Integer.min(graph.power[it], myConst1));
			} else {
				pp = 0;
				qq = 0;
				if (graph.power[it] > 35) {
					if (sup[it] < 13) {
						for (Integer ie : graph.adjacencyList[it])
							if (colori[ie] == graph.enemyColor)
								pp++;
						if (pp >= 2 && graph.power[it] - 13 + sup[it] >= graph.power[ir])
							qq = 1;
					}
				}
				if (qq == 0)
					addOrder(it, ir, graph.power[it]);
				else
					addOrder(it, ir, graph.power[it] - 13 + sup[it]);
			}
			attacked[ir] = 1;
			colori[ir] = graph.myColor;
		} else {
			for (Integer is : graph.adjacencyList[iw])
				if (colori[is] == graph.enemyColor)
					flagb = 1;
			if (graph.color[iw] == -1 && flagb == 0) {
				if (graph.safeDistance[graph.enemyColor][iw] == Integer.MAX_VALUE / 2)
					addOrder(it, iw, 1);
				else
					addOrder(it, iw, Integer.min(graph.power[it], myConst1));
			} else {
				// System.out.println(" qq " + it + " ww " + graph.power[it] + "
				// ee " + graph.power[iw]);
				flagb = 0;
				for (Integer is : graph.adjacencyList[iw]) {
					if (graph.color[is] == -1)
						flaga = -1;
					if (graph.color[is] == graph.myColor)
						flagb += 1;
				}
				if (((graph.power[it] + sup[it] > 10 && graph.power[it] <= 10)
						|| (graph.power[it] + sup[it] > 30 && graph.power[it] <= 30)) && (flaga != -1)
						&& (attacked[iw] == 0) && ((flagb < 2) || (graph.xpower[iw] - graph.xpower[it] > 1))
						&& ((graph.xpower[iw] != 0) || (graph.power[it] < 6))) {
					// addOrder(it, iw, graph.power[it]);
					/// System.out.println(" DEF ");
					// System.out.println(" qq " + it + " ww " + iw
					// + " ee " + graph.power[it] + " tt "
					// + graph.power[iw]);
				} else
					addOrder(it, iw, graph.power[it]);
			}
		}
	}

	private void unsupportedWarNode(Integer it) {
		Integer ir = -1;
		Integer iw = -1;
		Integer flaga = 0;
		Integer flagb = 0;
		Integer flagc = 0;
		Integer flagd = 0;
		Integer minn = Integer.MAX_VALUE / 2 - 1;
		for (Integer ie : graph.adjacencyList[it]) {
			if (colori[ie] != graph.myColor) {
				flaga += 1;
				if (graph.power[ie] < minn) {
					minn = graph.power[ie];
					iw = ie;
				}
			}
		}
		// no enemy or empty!(in guess)
		if (iw == -1) {
			for (Integer ie : graph.adjacencyList[it]) {
				if (graph.color[ie] != graph.myColor) {
					flaga += 1;
					if (graph.power[ie] < minn) {
						minn = graph.power[ie];
						iw = ie;
					}
				}
			}
		}
		// one enemy-empty (in guess)
		if (flaga == 1) {
			for (Integer is : graph.adjacencyList[iw])
				if (colori[is] == graph.enemyColor)
					flagb = 1;
			if (graph.color[iw] == -1 && flagb == 0) {
				if (graph.safeDistance[graph.enemyColor][iw] == Integer.MAX_VALUE / 2) {
					addOrder(it, iw, 1);
					if (1 >= graph.power[iw]) {
						colori[iw] = graph.myColor;
						attacked[iw] = 1;
					}
				} else {
					addOrder(it, iw, Integer.min(graph.power[it], myConst1));
					if (Integer.min(graph.power[it], myConst1) >= graph.power[iw]) {
						colori[iw] = graph.myColor;
						attacked[iw] = 1;
					}
				}
			} else {
				flagd = 0;
				for (Integer ie : graph.adjacencyList[iw])
					if (graph.xpower[ie] >= 2)
						flagd = 1;
				if (graph.power[iw] < 11 && flagd == 0)
					addOrder(it, iw, Integer.min(graph.power[it], 33));
				else
					addOrder(it, iw, graph.power[it]);
				if (graph.power[it] >= graph.power[iw]) {
					colori[iw] = graph.myColor;
					attacked[iw] = 1;
				}
			}
		}
		// more enemy-empty (in guess)
		else {
			for (Integer is : graph.adjacencyList[iw])
				if (colori[is] == graph.enemyColor)
					flagb = 1;
			for (Integer is : graph.adjacencyList[it])
				if (colori[is] == graph.enemyColor)
					flagc += 1;
			if (flagc == 1) {
				for (Integer is : graph.adjacencyList[it])
					if (colori[is] == graph.enemyColor)
						ir = is;
				if (graph.power[it] >= graph.power[ir]) {
					flagd = 0;
					for (Integer ie : graph.adjacencyList[ir])
						if (graph.xpower[ie] >= 2)
							flagd = 1;
					if (graph.power[ir] < 11 && flagd == 0)
						addOrder(it, ir, Integer.min(graph.power[it], 33));
					else
						addOrder(it, ir, graph.power[it]);
					if (graph.power[it] >= graph.power[ir]) {
						colori[ir] = graph.myColor;
						attacked[ir] = 1;
					}
				} else {
					flagc = 0;
				}
			}
			if (flagc != 1) {
				if (graph.color[iw] == -1 && flagb == 0) {
					if (graph.safeDistance[graph.enemyColor][iw] == Integer.MAX_VALUE / 2) {
						addOrder(it, iw, 1);
						if (1 >= graph.power[iw]) {
							colori[iw] = graph.myColor;
							attacked[iw] = 1;
						}
					} else {
						addOrder(it, iw, Integer.min(graph.power[it], myConst1));
						if (Integer.min(graph.power[it], myConst1) >= graph.power[iw]) {
							colori[iw] = graph.myColor;
							attacked[iw] = 1;
						}
					}
				} else {
					addOrder(it, iw, Integer.max((graph.power[it] + 1) / 2, graph.power[it] - 33));
					if (Integer.max((graph.power[it] + 1) / 2, graph.power[it] - 33) >= graph.power[iw]) {
						attacked[iw] = 1;
						colori[iw] = graph.myColor;
					}
				}
			}
		}
	}
}
