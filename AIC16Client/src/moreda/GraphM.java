package moreda;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import client.World;
import client.model.Graph;
import client.model.Node;

public class GraphM implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 358286894682265844L;

	public Guess guess;

	public int size;
	public int[][] distance;
	public int[][] path;
	public int[][] safeDistance;
	public int[][] parent;
	public int[][] near;
	public int[][] xsafeDistance;
	public int[][] xparent;
	public int[][] xnear;
	public int[] psafeDistance;
	public int[] pparent;
	public int[] pnear;

	public int[] color;
	public int[] power;
	public int[] xpower;
	public intVector[] adjacencyList;
	public intVector[] nodeS;
	public intVector[] outter;
	public int[][] isOutter;
	public int[][] isSafe;

	public int myColor;
	public int enemyColor;
	public int totalTurns;
	public long totalTurnTime;
	public int escapeConstant;
	public int nodeBonusConstant;
	public int edageBonusConstant;
	public int lowArmyBound;
	public int mediumArmyBound;
	public double lowWarDamage;
	public double mediumWarDamage;
	public int thisTurn;

	public intVector inFirst;
	public intVector borderFirst;
	public intVector border;
	public intVector in;
	public int[] underAttack;

	public boolean first = true;

	/*
	 * O(m+n)->O(n)
	 */
	public GraphM(World world) {
		size = world.getMap().getNodes().length;
		distance = new int[size][size];
		path = new int[size][size];
		safeDistance = new int[2][size];
		near = new int[2][size];
		parent = new int[2][size];
		xsafeDistance = new int[2][size];
		xnear = new int[2][size];
		xparent = new int[2][size];
		psafeDistance = new int[size];
		pnear = new int[size];
		pparent = new int[size];
		color = new int[size];
		power = new int[size];
		adjacencyList = new intVector[size];
		outter = new intVector[2];
		isOutter = new int[2][size];
		isSafe = new int[2][size];
		xpower = new int[size];
		border = new intVector();
		borderFirst = new intVector();
		in = new intVector();
		inFirst = new intVector();
		underAttack = new int[size];
		guess = new Guess();
		nodeS = new intVector[] { new intVector(), new intVector() };
		outter = new intVector[] { new intVector(), new intVector() };
		for (int i = 0; i < adjacencyList.length; i++) {
			adjacencyList[i] = new intVector();
		}
		startGraph(world);
	}

	/*
	 * O(m+n)->O(n)
	 */
	private void startGraph(World world) {
		myColor = world.getMyID();
		enemyColor = 1 - myColor;
		totalTurns = world.getTotalTurns();
		totalTurnTime = world.getTotalTurnTime();
		escapeConstant = world.getEscapeConstant();
		nodeBonusConstant = world.getNodeBonusConstant();
		edageBonusConstant = world.getEdgeBonusConstant();
		lowArmyBound = world.getLowArmyBound();
		mediumArmyBound = world.getMediumArmyBound();
		mediumWarDamage = world.getMediumCasualtyCoefficient();
		lowWarDamage = world.getLowCasualtyCoefficient();
		Graph graph = world.getMap();
		for (int i = 0; i < size; ++i)
			for (Node node : graph.getNode(i).getNeighbours())
				adjacencyList[i].add(node.getIndex());
		Floyd_Warshall();
		updateGraph(world);
		setBorder(inFirst, borderFirst);
	}

	/*
	 * O(m+n)->O(n)
	 */
	public void updateGraph(World world) {
		try {
			guess.lastGraph = (GraphM) (ObjectCloner.deepCopy(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int it, ie;
		thisTurn = world.getTurnNumber();
		nodeS[0].clear();
		nodeS[1].clear();
		Node[] nodes = world.getMap().getNodes();
		for (Node node : nodes) {
			it = node.getIndex();
			ie = node.getOwner();
			if (ie != -1)
				nodeS[ie].add(it);
			color[it] = ie;
			if (first && ie == myColor)
				guess.score += node.getArmyCount();
			xpower[it] = node.getArmyCount();
		}
		guess.graph = this;
		if (!first)
			guess.update();
		first = false;
		for (Node node : nodes) {
			it = node.getIndex();
			ie = node.getOwner();
			if (ie == enemyColor)
				power[it] = guess.get(node.getArmyCount());
			else
				power[it] = node.getArmyCount();
			xpower[it] = node.getArmyCount();
		}
		BFS(nodeS[0], nodeS[1], parent[0], near[0], safeDistance[0]);
		BFS(nodeS[1], nodeS[0], parent[1], near[1], safeDistance[1]);
		BFS(nodeS[0], null, xparent[0], xnear[0], xsafeDistance[0]);
		BFS(nodeS[1], null, xparent[1], xnear[1], xsafeDistance[1]);
		setBorder(in, border);
		BFS(border, null, pparent, pnear, psafeDistance);
		setOutter();
	}

	/*
	 * O(m+n)->O(n)
	 */
	public void updateGraph() {
		int ie;
		nodeS[0].clear();
		nodeS[1].clear();
		for (int it = 0; it < size; ++it) {
			ie = color[it];
			if (ie != -1)
				nodeS[ie].add(it);
		}
		BFS(nodeS[0], nodeS[1], parent[0], near[0], safeDistance[0]);
		BFS(nodeS[1], nodeS[0], parent[1], near[1], safeDistance[1]);
		BFS(nodeS[0], null, xparent[0], xnear[0], xsafeDistance[0]);
		BFS(nodeS[1], null, xparent[1], xnear[1], xsafeDistance[1]);
		setBorder(in, border);
		BFS(border, null, pparent, pnear, psafeDistance);
		setOutter();
	}

	/*
	 * Floyd Warshall calculate all pair shortest path on distance and path.
	 * O(n^3)
	 */
	private void Floyd_Warshall() {
		for (int i = 0; i < size; ++i)
			for (int j = 0; j < size; ++j) {
				distance[i][j] = Integer.MAX_VALUE / 2;
				path[i][j] = -1;
			}
		for (int i = 0; i < size; ++i)
			for (Integer adjNode : adjacencyList[i])
				distance[i][adjNode] = 1;

		for (int k = 0; k < size; ++k)
			for (int i = 0; i < size; ++i)
				for (int j = 0; j < size; ++j)
					if (distance[i][k] + distance[k][j] < distance[i][j]) {
						path[i][j] = k;
						distance[i][j] = distance[i][k] + distance[k][j];
					}
	}

	/*
	 * Normal BFS with start nodes and block nodes that dosen't pass the block
	 * nodes. parent and near and distance as parameter that set in the BFS.
	 * O(m+n)->O(n)
	 */
	private void BFS(intVector start, intVector block, int[] parent, int[] near, int distance[]) {
		Queue<Integer> queue = new LinkedList<Integer>();
		int[] mark = new int[size];
		Integer ie;
		for (int i = 0; i < size; ++i) {
			mark[i] = 0;
			parent[i] = -1;
			near[i] = -1;
			distance[i] = Integer.MAX_VALUE / 2;
		}
		for (Integer it : start) {
			queue.add(it);
			mark[it] = 1;
			distance[it] = 0;
			near[it] = it;
		}
		if (block != null)
			for (Integer it : block)
				mark[it] = -1;
		while (!queue.isEmpty()) {
			ie = queue.poll();
			for (Integer it : adjacencyList[ie]) {
				if (mark[it] != 1) {
					parent[it] = ie;
					near[it] = near[ie];
					distance[it] = distance[ie] + 1;
					if (mark[it] == 0)
						queue.add(it);
					mark[it] = 1;
				}
			}
		}
	}

	/*
	 * O(n)
	 */
	public intVector getPath(int source, int destination) {
		intVector aPath = new intVector();
		if (distance[source][destination] != Integer.MAX_VALUE / 2) {
			aPath.add(source);
			fullPath(source, destination, aPath);
			aPath.add(destination);
			return aPath;
		}
		return null;
	}

	/*
	 * O(n)
	 */
	private void fullPath(int source, int destination, intVector aPath) {
		if (distance[source][destination] != 1) {
			fullPath(source, path[source][destination], aPath);
			aPath.add(path[source][destination]);
			fullPath(path[source][destination], destination, aPath);
		}
	}

	/*
	 * O(n)
	 */
	public intVector getPath(int destination, int[] parent) {
		intVector aPath = new intVector();
		getPath(destination, parent, aPath);
		return aPath;
	}

	/*
	 * O(n)
	 */
	private void getPath(int destination, int[] parent, intVector aPath) {
		if (parent[destination] != -1)
			getPath(parent[destination], parent, aPath);
		aPath.add(destination);
	}

	/*
	 * O(n)
	 */
	private void setBorder(intVector in, intVector border) {
		in.clear();
		border.clear();
		for (int i = 0; i < size; ++i) {
			if (safeDistance[myColor][i] < safeDistance[enemyColor][i])
				in.add(i);
			if ((safeDistance[myColor][i] == safeDistance[enemyColor][i]
					&& safeDistance[myColor][i] != Integer.MAX_VALUE / 2)
					|| (safeDistance[myColor][i] == safeDistance[enemyColor][i] - 1)
					|| (safeDistance[myColor][i] == safeDistance[enemyColor][i] - 1))
				border.add(i);
		}
	}

	/*
	 * O(m+n)->O(n)
	 */
	private void setOutter() {
		outter[0].clear();
		outter[1].clear();
		for (int i = 0; i < size; ++i) {
			isOutter[0][i] = 0;
			isOutter[1][i] = 0;
			isSafe[0][i] = 1;
			isSafe[1][i] = 1;
		}

		for (Integer it : nodeS[0]) {
			for (Integer ie : adjacencyList[it])
				if (color[ie] != 0) {
					isOutter[0][it] = 1;
					if (color[ie] == 1)
						isSafe[0][it] = 0;
				}
			if (isOutter[0][it] == 1)
				outter[0].add(it);
		}
		for (Integer it : nodeS[1]) {
			for (Integer ie : adjacencyList[it])
				if (color[ie] != 1) {
					isOutter[1][it] = 1;
					if (color[ie] == 0)
						isSafe[1][it] = 0;
				}
			if (isOutter[1][it] == 1)
				outter[1].add(it);
		}
	}
}
