package CHOW0008;


import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.clients.TeamClient;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Star;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
	HashMap<UUID, Boolean> goingForCore;
	//@Override
	public void initialize(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

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
		Ship starShip;
		//starShip = getStarCollector(space, actionableObjects);
		for (AbstractObject actionable : actionableObjects) {
				if (actionable instanceof Ship) {
					starShip = (Ship) actionable;
					AbstractAction action;
					Star star = findNearestStar(space, starShip);//finds nearest star
					action = new MoveToObjectAction(space, starShip.getPosition(), star);// command to move ship to star
					actions.put(starShip.getId(), action);
					//action = getStarAction(space, starShip);
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
	private Star findNearestStar(Toroidal2DPhysics space, Ship ship){
		double minDistance = Double.MAX_VALUE;
		Star nearestStar = null;
			for (Star star : space.getStars()){
				double dist = space.findShortestDistance(ship.getPosition(), star.getPosition());
				if (dist < minDistance){
					minDistance = dist;
					nearestStar = star;
				}
			}
			return nearestStar;
	}


	//@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
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
