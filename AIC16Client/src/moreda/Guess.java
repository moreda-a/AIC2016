package moreda;

import java.io.Serializable;

public class Guess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3946125803674737600L;
	public GraphM graph;
	public GraphM lastGraph;

	public int score = 0;
	private int weakCount = 5;
	private int normalCount = 20;
	private int strongCount = 40;

	public int get(int armyCount) {
		switch (armyCount) {
		case 0:
			return weakCount;
		case 1:
			return normalCount;
		case 2:
			return strongCount;
		default:
			return 0;
		}
	}

	/*
	 * faghat namosan az graph i ke dastet hich sho set nakon serfan begir
	 * azash!
	 */
	public void update() {
		intVector enemyNodes = graph.nodeS[graph.enemyColor];
		intVector myNodes = graph.nodeS[graph.myColor];
		int myIncome = 0, damage = 0;// , lastArmy = 0, nowArmy = 0;
		int weak = 0, normal = 0, strong = 0;
		int myId = graph.myColor;
		int enemyId = !(myId == 1) ? 1 : 0;
		for (Integer node : myNodes) {
			myIncome = 0;
			if (lastGraph.color[node] != myId) {
				myIncome = myIncome + graph.nodeBonusConstant;
				// System.out.println(node + " + !node! " +
				// graph.nodeBonusConstant);
			}

			for (int i = 0; i < graph.adjacencyList[node].size(); i++) {
				if (graph.color[node] == myId) {
					myIncome = myIncome + graph.edageBonusConstant;
					// System.out.println(node + " + !edge! " +
					// graph.edageBonusConstant);
					// break;
				}
			}
			damage = damage + lastGraph.xpower[node] - graph.xpower[node] + myIncome;
			// lastArmy += lastGraph.xpower[node];
			// nowArmy += graph.xpower[node];
		}
		// System.out.println( "last army: " + lastArmy + " now army: "+ nowArmy
		// + " income: "+ myIncome);
		for (Integer node : enemyNodes) {
			if (graph.xpower[node] == 0) {
				weak++;
			}
			if (graph.xpower[node] == 1) {
				normal++;
			}
			if (graph.xpower[node] == 2) {
				strong++;
			}
			int index = node;
			if (lastGraph.color[index] != enemyId) {
				score = score + graph.nodeBonusConstant;

			}
			for (int i = 0; i < graph.adjacencyList[node].size(); i++) {
				if (graph.color[node] == enemyId) {
					score = score + graph.edageBonusConstant;

					// break;
				}
			}
		}
		score = score - damage;
		int enemyRemainArmyCount = score;

		weakCount = 1;
		normalCount = graph.lowArmyBound + 1;
		strongCount = graph.mediumArmyBound + 1;
		enemyRemainArmyCount = enemyRemainArmyCount - (weak * weakCount) - (normal * normalCount)
				- (strong * strongCount);
		// int turns = world.getTotalTurns(), turn = world.getTurnNumber();
		int addingAmount = enemyRemainArmyCount / (weak + normal + strong);
		if (addingAmount > graph.lowArmyBound - 1) {
			weakCount = graph.lowArmyBound;
			enemyRemainArmyCount = enemyRemainArmyCount - (weak * (graph.lowArmyBound - 1));
			if ((normal + strong) != 0) {
				addingAmount = enemyRemainArmyCount / (normal + strong);
			}
		} else if (addingAmount > 0) {
			weakCount = weakCount + addingAmount;
			enemyRemainArmyCount = enemyRemainArmyCount - (weak * addingAmount);
			if ((normal + strong) != 0) {
				addingAmount = enemyRemainArmyCount / (normal + strong);
			}
			// System.out.println("weak " + addingAmount);
		}
		if (addingAmount > graph.mediumArmyBound - graph.lowArmyBound - 1) {
			normalCount = graph.mediumArmyBound;
			enemyRemainArmyCount = enemyRemainArmyCount - (normal * (graph.mediumArmyBound - 1));
			if (strong != 0) {
				addingAmount = enemyRemainArmyCount / strong;
			}
		} else if (addingAmount > 0) {
			normalCount = normalCount + addingAmount;
			enemyRemainArmyCount = enemyRemainArmyCount - (normal * addingAmount);
			if (strong != 0) {
				addingAmount = enemyRemainArmyCount / strong;
			}
			// System.out.println("normal " + addingAmount);
		}
		if (addingAmount > 0 && strong != 0) {
			strongCount = strongCount + addingAmount;
			// System.out.println("strong " + addingAmount);
		}
		// System.out.println("dmg: " + damage + " Score: " + score + " weak: "
		// + weakCount + " normal: " + normalCount
		// + " strong: " + strongCount);
	}

}