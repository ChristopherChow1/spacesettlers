package CHOW0008;

import spacesettlers.actions.*;
import spacesettlers.clients.TeamClient;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.*;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.graphics.LineGraphics;
import spacesettlers.utilities.Vector2D;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Modified version of the beacon collector client.
 * instead of beacons, it will try to go for nodes.
 *
 *  
 * @author Chris
 */



public class ChowProject2TeamClient extends TeamClient {
	/**
	 * Map of the beacon to which ship is aiming for it
	 */
	HashMap<Beacon, Ship> beaconToShipMap;
	HashMap<Ship, Beacon> shipToBeaconMap;


	/**
	 * Send each ship to a beacon
	 */
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();
		seeGraphics(space, actionableObjects);


		// loop through each ship

		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				AbstractAction current = ship.getCurrentAction();

				// does the ship have a beacon it is aiming for?
				if (current == null || current.isMovementFinished(space) || !shipToBeaconMap.containsKey(ship)) {
					Position currentPosition = ship.getPosition();
					Beacon beacon = pickNearestFreeBeacon(space, ship);

					AbstractAction newAction = null;

					if (beacon == null) {
						// there is no beacon available so do nothing
						newAction = new DoNothingAction();
					} else {
						beaconToShipMap.put(beacon, ship);
						shipToBeaconMap.put(ship, beacon);
						Position newGoal = beacon.getPosition();
						newAction = new MoveToObjectAction(space, currentPosition, beacon);
					}
					actions.put(ship.getId(), newAction);
				} else {
					actions.put(ship.getId(), ship.getCurrentAction());
				}
			} else {
				// it is a base and Beacon collector doesn't do anything to bases
				actions.put(actionable.getId(), new DoNothingAction());
			}
		}
		return actions;
	}




	/**
	 * Find the nearest free beacon to this ship
	 * @param space
	 * @param ship
	 * @return
	 */
	private Beacon pickNearestFreeBeacon(Toroidal2DPhysics space, Ship ship) {
		// get the current beacons
		Set<Beacon> beacons = space.getBeacons();

		Beacon closestBeacon = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (Beacon beacon : beacons) {
			if (beaconToShipMap.containsKey(beacon)) {
				continue;
			}

			double dist = space.findShortestDistance(ship.getPosition(), beacon.getPosition());
			if (dist < bestDistance) {
				bestDistance = dist;
				closestBeacon = beacon;
			}
		}

		return closestBeacon;
	}




	/**
	 * Clean up data structure including beacon maps
	 */
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {

		// once a beacon has been picked up, remove it from the list 
		// of beacons being pursued (so it can be picked up at its
		// new location)
		for (Beacon beacon : space.getBeacons()) {
			if (!beacon.isAlive()) {
				shipToBeaconMap.remove(beaconToShipMap.get(beacon));
				beaconToShipMap.remove(beacon);
			}
		}

	}


	private ArrayList<SpacewarGraphics> graphicsToAdd;




	public void initialize(Toroidal2DPhysics space) {
		//beaconToShipMap = new HashMap<Beacon, Ship>();
		//shipToBeaconMap = new HashMap<Ship, Beacon>();
		graphicsToAdd = new ArrayList<SpacewarGraphics>();
		//seeGraphics(space);

	}

	@Override
	public void shutDown(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

	}

	@Override

	public Set<SpacewarGraphics> getGraphics() {
		// TODO Auto-generated method stub
		HashSet<SpacewarGraphics> graphics = new HashSet<SpacewarGraphics>();
		graphics.addAll(graphicsToAdd);
		graphicsToAdd.clear();
		return graphics;
	}
	public void seeGraphics(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects){
		Graph nodesGraph = new Graph();
		Set<Beacon> beacons = space.getBeacons();

		Set<Star> stars = space.getStars();
		List<Beacon> beaconList = new ArrayList<>(beacons);
		Beacon beacon1 = beaconList.get(0);// pick one beacon as a goal. kinda random
		int i = 0;
		LineGraphics lines;
		ArrayList<Edge> edgeArrayList = new ArrayList<Edge>();
		ArrayList<Position> nodesList = new ArrayList<>();


		for (AbstractObject actionable : actionableObjects){
			if (actionable instanceof Ship){
				Ship ship = (Ship) actionable;
				Position shipPos = ship.getPosition();
				nodesGraph.addNode(shipPos, space);//have ship's position be the first entry
				nodesList.add(shipPos);
				Vector2D currentVelocity = shipPos.getTranslationalVelocity();
				RawAction action = null;
				double angularVal = shipPos.getAngularVelocity();
				for (Star tempStar : stars){
					//connects the ship to the rest of the stars
					nodesGraph.addNode(tempStar.getPosition(), space);
					nodesList.add(tempStar.getPosition());
					Edge shipToStar = new Edge(shipPos, tempStar.getPosition(), space);
					edgeArrayList.add(shipToStar);
					lines = new LineGraphics(ship.getPosition(), tempStar.getPosition(), space.findShortestDistanceVector(ship.getPosition(), tempStar.getPosition()));
					graphicsToAdd.add(lines);


					nodesGraph.addNode(tempStar.getPosition(), space);
					//graphicsToAdd.add(new TargetGraphics(20, super.getTeamColor(), tempStar.getPosition()));
					for (Star tempStar2 : stars){
						//I need to make a function to record edges.
						Edge tempEdge = new Edge(tempStar.getPosition(),tempStar2.getPosition(), space);
						edgeArrayList.add(tempEdge);
						//nodesGraph.addEdge(tempEdge, space);// this is making my graph invisible why?

						lines = new LineGraphics(tempStar.getPosition(), tempStar2.getPosition(), space.findShortestDistanceVector(tempStar.getPosition(), tempStar2.getPosition()));
						graphicsToAdd.add(lines);
					}
					///let one energy beacon be the goal.

					lines = new LineGraphics(tempStar.getPosition(), beacon1.getPosition(), space.findShortestDistanceVector(tempStar.getPosition(), beacon1.getPosition()));
					Edge starToBeacon = new Edge(tempStar.getPosition(), beacon1.getPosition(), space);
					edgeArrayList.add(starToBeacon);
					graphicsToAdd.add(lines);
				}
				nodesGraph.addNode(beacon1.getPosition(),space); // make sure that the beacon is the last entry.






				//an attempt to do A* search
				ArrayList<Position> listA = new ArrayList<Position>();
				ArrayList<Position> ListB = new ArrayList<Position>();
				ArrayList<Edge> connectedEdges = new ArrayList<Edge>();
				Edge savedEdge = edgeArrayList.get(0);
				Position goalNode = nodesList.get(nodesList.size()-1);// goal node
				Position currentNode = shipPos;
				ArrayList<Position> path = new ArrayList<Position>();
				ArrayList<Edge> pathEdge = new ArrayList<Edge>();
				int f = 0,  g;
				double u = 1000000, w;

				double heuristic = space.findShortestDistance(shipPos, goalNode);

				//
				while (currentNode != goalNode){
					connectedEdges.clear();
					listA.add(currentNode);

					for (Edge tempEdge : edgeArrayList){
						if (tempEdge.getStart() == currentNode){
							connectedEdges.add(tempEdge);
						}
					}
					for (Edge currentEdge : connectedEdges){
						if (currentEdge.getDistance() < u) { // pick the smallest adjacent star.
							u = currentEdge.getDistance();
							savedEdge = currentEdge;
						}
					}

					currentNode = savedEdge.getEnd();
					path.add(currentNode);
					pathEdge.add(savedEdge);
					for (Edge steps : pathEdge){
						lines = new LineGraphics(steps.getStart(), steps.getEnd(), space.findShortestDistanceVector(steps.getStart(), steps.getEnd()));
						lines.setLineColor(Color.ORANGE);
						graphicsToAdd.add(lines);
						pathEdge.remove(0);
					}
				}
				// Why is this returning one line? Only the shortest line.
				// It took me way too long to get this to not break the previous graph.



			}
		}



	}




	@Override
	/**
	 * Beacon collector never purchases
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, 
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts) {
		return new HashMap<UUID,PurchaseTypes>();
	}


	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}

}
//Collection of vertices and edges
class Graph {
	ArrayList<Vertex> nodes;
	ArrayList<Edge> edges;


	public Graph(){
		this.nodes = new ArrayList<>();

	}
	public void addNode(Position node, Toroidal2DPhysics space) {
		Vertex newVert = new Vertex(node, space);
		this.nodes.add(newVert);
	}
	public void addEdge(Edge edge, Toroidal2DPhysics space){
		Edge newEdge = new Edge(edge.getStart(), edge.getEnd(), space);
		edges.add(newEdge);
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void removeVertex(Vertex vert){
		this.nodes.remove(vert);
	}
	public ArrayList<Vertex> getNodes(){
		return this.nodes;
	}
	public Vertex getNodeIndex(int index){
		return this.nodes.get(index);
	}



}
//Vertex class to record the positions of stuff on the map
class Vertex {
	Position vertex;
	private ArrayList<Edge> edges;

	Vertex(Position vertex, Toroidal2DPhysics space) {
		this.vertex = vertex;
		this.edges = new ArrayList<Edge>();
	}

	public void removeEdge(Vertex end){
		this.edges.removeIf(edge -> edge.getEnd().equals(end));
	}
}
//consists of two vertices
class Edge {
	private Position start;
	private Position end;

	private double distance;
	public Edge(Position vertA, Position vertB, Toroidal2DPhysics space){
		this.start = vertA;
		this.end = vertB;
		this.distance = space.findShortestDistance(vertA, vertB);
	}

	public Position getStart() {
		return this.start;
	}

	public Position getEnd() {
		return this.end;
	}

	public Double getDistance(){
		return this.distance;
	}
}
