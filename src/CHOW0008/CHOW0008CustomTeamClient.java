package CHOW0008;


import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.clients.TeamClient;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.game.HeuristicGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Star;
import spacesettlers.objects.*;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.AiCore;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Drone;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

import java.util.*;

/**
 * Client that literally never moves (not a horrible strategy if you just want to never die)
 * @author amy
 *
 *
 *
 */

/**
 * This client is a modified version of the DoNothing client.
 * Imma bang my head against this.
 * @author chris
 */
public class CHOW0008CustomTeamClient extends TeamClient {
// for some reason TeamClient is not being read.
	HashMap <UUID, Ship> starToShipMap;
	HashMap <UUID, Ship> asteroidToShipMap;
	HashMap<UUID, Boolean> goingForCore;

	HashMap <UUID, Boolean> aimingForBase;
	HashMap <UUID, Boolean> justHitBase;

	//@Override
	public void initialize(Toroidal2DPhysics space) {
		asteroidToShipMap = new HashMap<UUID, Ship>();
		aimingForBase = new HashMap<UUID, Boolean>();
		justHitBase = new HashMap<UUID, Boolean>();

	}

	//@Override
	public void shutDown(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

	}


	//Aicore nearbyStar = pickNearestStar

	//@Override
	//below is scuffed spaghetti code
	//Bethesda: "It just works."
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
													  Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();

		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				AbstractAction action;
				action = getAsteroidCollectorAction(space, ship);
				actions.put(ship.getId(), action);

			} else {
				// it is a base.  Heuristically decide when to use the shield (TODO)
				actions.put(actionable.getId(), new DoNothingAction());
			}
		}
		return actions;
	}
	//I probably don't need to use this code
	private Ship getStarCollector(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects){
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship){
				Ship ship = (Ship) actionable;
				return ship;
			}
		}
		return null;
	}
	private Beacon pickNearestBeacon(Toroidal2DPhysics space, Ship ship) {
		// get the current beacons
		Set<Beacon> beacons = space.getBeacons();

		Beacon closestBeacon = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (Beacon beacon : beacons) {
			double dist = space.findShortestDistance(ship.getPosition(), beacon.getPosition());
			if (dist < bestDistance) {
				bestDistance = dist;
				closestBeacon = beacon;
			}
		}

		return closestBeacon;
	}
	private AbstractAction getAsteroidCollectorAction(Toroidal2DPhysics space,
													  Ship ship) {
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();

		// aim for a beacon if there isn't enough energy
		if (ship.getEnergy() < 2000) {
			Beacon beacon = pickNearestBeacon(space, ship);
			AbstractAction newAction = null;
			// if there is no beacon, then just skip a turn
			if (beacon == null) {
				newAction = new DoNothingAction();
			} else {
				newAction = new MoveToObjectAction(space, currentPosition, beacon);
			}
			aimingForBase.put(ship.getId(), false);
			return newAction;
		}

		// if the ship has enough resourcesAvailable, take it back to base
		if (ship.getResources().getTotal() > 500) {
			Base base = findNearestBase(space, ship);
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, base);
			aimingForBase.put(ship.getId(), true);
			return newAction;
		}

		// otherwise aim for the nearest gaming asteroid
		if (current == null || current.isMovementFinished(space) ||
				(justHitBase.containsKey(ship.getId()) && justHitBase.get(ship.getId()))) {
			justHitBase.put(ship.getId(), false);
			aimingForBase.put(ship.getId(), false);
			Asteroid asteroid = pickHighestValueNearestFreeAsteroidGamingIfPossible(space, ship);

			AbstractAction newAction = null;

			if (asteroid != null) {
				asteroidToShipMap.put(asteroid.getId(), ship);
				newAction = new MoveToObjectAction(space, currentPosition, asteroid,
						asteroid.getPosition().getTranslationalVelocity());
			}

			return newAction;
		}

		return ship.getCurrentAction();
	}
	private Base findNearestBase(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.MAX_VALUE;
		Base nearestBase = null;

		for (Base base : space.getBases()) {
			if (base.getTeamName().equalsIgnoreCase(ship.getTeamName())) {
				double dist = space.findShortestDistance(ship.getPosition(), base.getPosition());
				if (dist < minDistance) {
					minDistance = dist;
					nearestBase = base;
				}
			}
		}
		return nearestBase;
	}

	private Asteroid pickHighestValueNearestFreeAsteroidGamingIfPossible(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		int bestMoney = Integer.MIN_VALUE;
		Asteroid bestAsteroid = null;
		double minDistance = Double.MAX_VALUE;
		HashSet<AbstractObject> obstructions = new HashSet<>(); //List of obstructions that block edges
		obstructions.addAll(getNonMineableAsteroids(space)); //Add non-mineable asteroids to obstructions
		obstructions.addAll(findNonTeamBases(space, teamName));


		for (Asteroid asteroid : asteroids) {
			if (!asteroidToShipMap.containsKey(asteroid.getId())) {
				if (asteroid.isMineable() && asteroid.getResources().getTotal() > bestMoney && asteroid.isGameable() && space.isPathClearOfObstructions(ship.getPosition(), asteroid.getPosition(), obstructions, 30)) {
					double dist = space.findShortestDistance(asteroid.getPosition(), ship.getPosition());
					if (dist < minDistance) {
						bestMoney = asteroid.getResources().getTotal();
						//System.out.println("Considering asteroid " + asteroid.getId() + " as a best one");
						bestAsteroid = asteroid;
						minDistance = dist;
					}
				}
			}
		}

		// if there was no gaming one available, then just return a regular one
		if (bestAsteroid == null) {
			return pickHighestValueNearestFreeAsteroid(space, ship);
		}

		//System.out.println("Best asteroid has " + bestMoney);
		return bestAsteroid;
	}
	private Asteroid pickHighestValueNearestFreeAsteroid(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		int bestMoney = Integer.MIN_VALUE;
		Asteroid bestAsteroid = null;
		double minDistance = Double.MAX_VALUE;

		for (Asteroid asteroid : asteroids) {
			if (!asteroidToShipMap.containsKey(asteroid.getId())) {
				if (asteroid.isMineable() && asteroid.getResources().getTotal() > bestMoney) {
					double dist = space.findShortestDistance(asteroid.getPosition(), ship.getPosition());
					if (dist < minDistance) {
						bestMoney = asteroid.getResources().getTotal();
						//System.out.println("Considering asteroid " + asteroid.getId() + " as a best one");
						bestAsteroid = asteroid;
						minDistance = dist;
					}
				}
			}
		}
		//System.out.println("Best asteroid has " + bestMoney);
		return bestAsteroid;
	}
	public Set<Base> findNonTeamBases(Toroidal2DPhysics space, String teamName) {
		Set<Base> bases = space.getBases(); //Base set of all bases
		Set<Base> bases2 = new HashSet<>(); //Solution set

		for(Base base : bases) { //Iterate through all bases
			if(!base.getTeamName().equals(teamName)) { //Check if it's our base
				bases2.add(base); //Add if not ours
			}
		}

		return bases2; //Return set of all non-team bases
	}
	public Set<Asteroid> getNonMineableAsteroids(Toroidal2DPhysics space) {
		Set<Asteroid> asteroids = space.getAsteroids(); //Base set of all asteroids
		Set<Asteroid> asteroids2 = new HashSet<>(); //Solution set to return

		for(Asteroid asteroid : asteroids) {
			if(!asteroid.isMineable()) {
				asteroids2.add(asteroid); //Add non-mineable asteroids to solution
			}
		}

		return asteroids2; //Return solution list
	}


	//@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {
		ArrayList<Asteroid> finishedAsteroids = new ArrayList<Asteroid>();

		for (UUID asteroidId : asteroidToShipMap.keySet()) {
			Asteroid asteroid = (Asteroid) space.getObjectById(asteroidId);
			if (asteroid != null && (!asteroid.isAlive() || asteroid.isMoveable())) {
				finishedAsteroids.add(asteroid);
				//System.out.println("Removing asteroid from map");
			}
		}

		for (Asteroid asteroid : finishedAsteroids) {
			asteroidToShipMap.remove(asteroid.getId());
		}

		// check to see who bounced off bases
		for (UUID shipId : aimingForBase.keySet()) {
			if (aimingForBase.get(shipId)) {
				Ship ship = (Ship) space.getObjectById(shipId);
				if (ship.getResources().getTotal() == 0 ) {
					// we hit the base (or died, either way, we are not aiming for base now)
					//System.out.println("Hit the base and dropped off resources");
					aimingForBase.put(shipId, false);
					justHitBase.put(shipId, true);
				}
			}
		}


	}

	//@Override
	public Set<SpacewarGraphics> getGraphics() {
		// TODO Auto-generated method stub
		return null;
	}


	//@Override
	/**
	 * Do nothing never purchases
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, 
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts) {
		// TODO Auto-generated method stub
		return new HashMap<UUID,PurchaseTypes>();
	}

	//@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
													  Set<AbstractActionableObject> actionableObjects) {
		HeuristicGameAgent agent = new HeuristicGameAgent();

		HashMap<UUID, AbstractGameAgent> actions = new HashMap<UUID, AbstractGameAgent>();
		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			actions.put(actionable.getId(), agent);
		}

		return actions;

	}
	//getStarAction is based off of line 190 om PacifistFlagCollector
	private AbstractAction getStarAction(Toroidal2DPhysics space, Ship ship){
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();
		Star star = pickNearestStar(space, ship, 200);
		AbstractAction newAction = null;


		if (star == null){
			newAction = new DoNothingAction();
		} else{
			newAction = new MoveToObjectAction(space, currentPosition, star);
		}
		return newAction;
	}

	private Star pickNearestStar(Toroidal2DPhysics space, Ship ship, int minimumDistance){
		Set<Star> stars = space.getStars();
		Star closestStar = null;
		double bestDistance = minimumDistance;

		for (Star star : stars){
			double dist = space.findShortestDistance(ship.getPosition(), star.getPosition());
			if (dist < bestDistance){
				bestDistance = dist;
				closestStar = star;
			}
		}
		return closestStar;
	}
	//private Ship getStarCarrier

}
