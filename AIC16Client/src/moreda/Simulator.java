package moreda;

import java.util.ArrayList;
import java.util.Collections;

public class Simulator {

	private GraphM graph;

	public GraphM runOn(GraphM graph, ArrayList<Order> orders, ArrayList<Order> opOrders) {
		try {
			this.graph = (GraphM) (ObjectCloner.deepCopy(graph));
		} catch (Exception e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		ArrayList<Order>[] allOrder = new ArrayList[2];
		allOrder[graph.myColor] = orders;
		allOrder[graph.enemyColor] = opOrders;
		// for (Order order : orders)
		// System.out.println(" input : " + order.getSource() + " | " +
		// order.getDestination() + " | " + order.getCount());

		simulateEvents(allOrder);

		// for (Integer it : this.graph.nodeS[graph.myColor])
		// System.out.println(" output : " + it + " | " + this.graph.power[it] +
		// " | " + graph.power[it]);
		return this.graph;
	}

	private boolean isMoveValid(int src, int dst, int armySize, int clientNum, int[] ownership, int[] armyCount) {
		int vertexNum = graph.size;
		if (src < 0 || src > vertexNum - 1)
			return false;
		if (dst < 0 || dst > vertexNum - 1)
			return false;
		if (clientNum != ownership[src])
			return false;
		if (graph.distance[src][dst] != 1)
			return false;
		if (armySize > armyCount[src] || armySize <= 0)
			return false;
		return true;
	}

	private int qualAmount(int amount) {
		if (amount <= graph.lowArmyBound) {
			return 0;
		}
		if (amount <= graph.mediumArmyBound) {
			return 1;
		}
		return 2;
	}

	private int[] doBattle(char type, int endp0, int endp1, int armySize0, int armySize1) {
		int more;
		int less;
		if (armySize0 == armySize1) {
			return new int[] { -1, 0 };
		}
		int[] output = new int[2];
		if (armySize0 > armySize1) {
			output[0] = 0;
			more = armySize0;
			less = armySize1;
		} else {
			output[0] = 1;
			more = armySize1;
			less = armySize0;
		}
		if (qualAmount(more) == qualAmount(less)) {
			output[1] = more - (int) Math.ceil((double) less * 1.0);
		} else if (qualAmount(more) - 1 == this.qualAmount(less)) {
			output[1] = more - (int) Math.ceil((double) less * graph.mediumWarDamage);
		} else if (qualAmount(more) - 2 == qualAmount(less)) {
			output[1] = more - (int) Math.ceil((double) less * graph.lowWarDamage);
		}
		return output;
	}

	private void simulateEvents(ArrayList<Order>[] clientsEvent) {
		int i;
		int i2;
		int vertexNum = graph.size;
		int[] armyCount = graph.power;
		int[] ownership = graph.color;
		int[] movesDest = new int[vertexNum];
		int[] movesDest2 = new int[vertexNum];
		int[] movesSize = new int[vertexNum];
		int[] movesSize2 = new int[vertexNum];
		int[][] armyInV = new int[2][vertexNum];
		int[] conflictedMoves = new int[vertexNum];

		int increaseWithEdge = graph.edageBonusConstant;
		int escapeNum = graph.escapeConstant;
		int increaseWithOwnership = graph.nodeBonusConstant;

		for (i2 = 0; i2 < vertexNum; ++i2) {
			movesDest[i2] = -1;
			movesDest2[i2] = -1;
			if (ownership[i2] <= -1)
				continue;
			armyInV[ownership[i2]][i2] = armyCount[i2];
		}
		if (!(clientsEvent[0] != null && clientsEvent[0].size() != 0
				|| clientsEvent[1] != null && clientsEvent[1].size() != 0)) {
			for (i2 = 0; i2 < vertexNum; ++i2) {
				if (ownership[i2] == -1)
					continue;
				for (int j = 0; j < graph.adjacencyList[i2].size(); ++j) {
					if (ownership[graph.adjacencyList[i2].get(j)] != ownership[i2])
						continue;
					int[] arrn = armyCount;
					int n = i2;
					arrn[n] = arrn[n] + increaseWithEdge;
				}
			}
			return;
		}
		for (int j = 0; j < 2; ++j) {
			if (clientsEvent[j] == null || clientsEvent[j].size() == 0)
				continue;
			for (i = clientsEvent[j].size() - 1; i > -1; --i) {
				int src = -1;
				int dst = -1;
				int armySize = -1;
				try {
					src = Integer.valueOf(clientsEvent[j].get(i).getSource());
					dst = Integer.valueOf(clientsEvent[j].get(i).getDestination());
					armySize = Integer.valueOf(clientsEvent[j].get(i).getCount());
				} catch (Exception e) {
					// Log.w("Flows", "Bad event received.", e);
				}
				if (!isMoveValid(src, dst, armySize, j, ownership, armyCount) || movesDest[src] >= 0) {
					System.err.println(" s: " + src + " d: " + dst + " c: " + armySize);
					continue;
				}
				movesDest[src] = dst;
				movesSize[src] = armySize;
				movesDest2[src] = dst;
				movesSize2[src] = armySize;
				int[] arrn = armyInV[j];
				int n = src;
				arrn[n] = arrn[n] - armySize;
				if (movesDest[dst] != src || ownership[dst] == ownership[src])
					continue;
				int[] battleInfo = doBattle('e', dst, src, movesSize[dst], armySize);
				conflictedMoves[src] = 1;
				conflictedMoves[dst] = 1;

				if (ownership[src] == battleInfo[0]) {
					movesSize[src] = battleInfo[1];
					movesSize[dst] = 0;
					continue;
				}
				if (ownership[dst] == battleInfo[0]) {
					movesSize[dst] = battleInfo[1];
					movesSize[src] = 0;
					continue;
				}
				movesSize[src] = 0;
				movesSize[dst] = 0;
			}
		}
		for (i2 = 0; i2 < vertexNum; ++i2) {
			if (ownership[i2] <= -1 || movesDest[i2] <= -1)
				continue;
			int[] arrn = armyInV[ownership[i2]];
			int n = movesDest[i2];
			arrn[n] = arrn[n] + movesSize[i2];
			if (conflictedMoves[i2] != 0)
				continue;
		}
		int[][] armyInVTemp = new int[2][vertexNum];
		for (i = 0; i < vertexNum; ++i) {
			int j2;
			if (armyInV[0][i] <= 0 || armyInV[1][i] <= 0)
				continue;
			int escaper = -1;
			if (armyInV[0][i] < armyInV[1][i]) {
				escaper = 0;
			} else if (armyInV[0][i] > armyInV[1][i]) {
				escaper = 1;
			}
			if (escaper == -1)
				continue;
			ArrayList<Integer> adjacencyListTemp = new ArrayList<Integer>(graph.adjacencyList[i].size());
			for (j2 = 0; j2 < graph.adjacencyList[i].size(); ++j2) {
				adjacencyListTemp.add(graph.adjacencyList[i].get(j2));
			}
			Collections.shuffle(adjacencyListTemp);
			for (j2 = 0; j2 < adjacencyListTemp.size(); ++j2) {
				if (ownership[(Integer) adjacencyListTemp.get(j2)] != escaper || armyInV[escaper][i] <= 0)
					continue;
				if (armyInV[escaper][i] >= escapeNum) {
					int[] arrn = armyInV[escaper];
					int n = i;
					arrn[n] = arrn[n] - escapeNum;
					int[] arrn2 = armyInVTemp[escaper];
					int n2 = (Integer) adjacencyListTemp.get(j2);
					arrn2[n2] = arrn2[n2] + escapeNum;
				} else {
					int[] arrn = armyInVTemp[escaper];
					int n = (Integer) adjacencyListTemp.get(j2);
					arrn[n] = arrn[n] + armyInV[escaper][i];
					armyInV[escaper][i] = 0;
				}
			}
		}
		for (i = 0; i < 2; ++i) {
			for (int j3 = 0; j3 < vertexNum; ++j3) {
				int[] arrn = armyInV[i];
				int n = j3;
				arrn[n] = arrn[n] + armyInVTemp[i][j3];
			}
		}
		for (i = 0; i < vertexNum; ++i) {
			if (armyInV[0][i] > 0 && armyInV[1][i] > 0) {
				int[] battleInfo = doBattle('v', i, -1, armyInV[0][i], armyInV[1][i]);
				if (battleInfo[0] > -1) {
					if (ownership[i] != battleInfo[0]) {
						armyInV[battleInfo[0]][i] = battleInfo[1] + increaseWithOwnership;
					} else {
						armyInV[battleInfo[0]][i] = battleInfo[1];
					}
					ownership[i] = battleInfo[0];
					armyInV[(ownership[i] - 1) * -1][i] = 0;
				} else {
					armyInV[0][i] = 0;
					armyInV[1][i] = 0;
				}
			} else if (armyInV[0][i] > 0) {
				if (ownership[i] != 0) {
					int[] arrn = armyInV[0];
					int n = i;
					arrn[n] = arrn[n] + increaseWithOwnership;
				}
				ownership[i] = 0;
			} else if (armyInV[1][i] > 0) {
				if (ownership[i] != 1) {
					int[] arrn = armyInV[1];
					int n = i;
					arrn[n] = arrn[n] + increaseWithOwnership;
				}
				ownership[i] = 1;
			}
			armyCount[i] = Math.max(armyInV[0][i], armyInV[1][i]);
		}
		for (i = 0; i < vertexNum; ++i) {
			if (ownership[i] == -1)
				continue;
			for (int j4 = 0; j4 < graph.adjacencyList[i].size(); ++j4) {
				if (ownership[graph.adjacencyList[i].get(j4)] != ownership[i])
					continue;
				int[] arrn = armyCount;
				int n = i;
				arrn[n] = arrn[n] + increaseWithEdge;
			}
		}
		graph.power = armyCount;
		graph.color = ownership;
		graph.updateGraph();
	}

}
