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
import spacesettlers.graphics.StarGraphics;
import java.util.*;

/**
 * Modified version of the beacon collector client.
 * instead of beacons, it will try to go for nodes.
 *
 *  
 * @author Chris
 */



public class ChowBeaconCollectorTeamClient extends TeamClient {
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
		seeGraphics(space);


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
	public void seeGraphics(Toroidal2DPhysics space){
		Graph nodesGraph = new Graph();
		Set<Beacon> beacons = space.getBeacons();

		Set<Star> Stars = space.getStars();
		int i = 0;
		LineGraphics lines;
		ArrayList<Edge> edgeArrayList = new ArrayList<Edge>();
		//Beacon tempBeacon2;
		for (Beacon tempBeacon : beacons){
			nodesGraph.addNode(tempBeacon.getPosition());
			graphicsToAdd.add(new StarGraphics(100, super.getTeamColor(), tempBeacon.getPosition()));
			for (Beacon tempBeacon2 : beacons){
				//I need to make a function to record edges.
				nodesGraph.addEdge(tempBeacon.getPosition(),tempBeacon2.getPosition(), space);




				lines = new LineGraphics(tempBeacon.getPosition(), tempBeacon2.getPosition(), space.findShortestDistanceVector(tempBeacon.getPosition(), tempBeacon2.getPosition()));
				graphicsToAdd.add(lines);
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
	public Vertex addNode(Position node) {
		Vertex newVert = new Vertex(node);
		this.nodes.add(newVert);
		return newVert;
	}
	public void addEdge(Position vert1, Position vert2, Toroidal2DPhysics space){
		edges.add(new Edge(vert1, vert2, space));

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

	Vertex(Position vertex) {
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
