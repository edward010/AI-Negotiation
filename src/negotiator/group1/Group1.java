package negotiator.group1;


import java.util.List;
import java.util.Map;

import agents.anac.y2012.AgentLG.OpponentBids;
import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.UtilitySpace;

/**
 * This is your negotiation party.
 */
public class Group1 extends AbstractNegotiationParty {

	private OpponentBids party1opponentBids;
	private double[] party1Weights = null;
	/**
	 * Please keep this constructor. This is called by genius.
	 *
	 * @param utilitySpace Your utility space.
	 * @param deadlines The deadlines set for this negotiation.
	 * @param timeline Value counting from 0 (start) to 1 (end).
	 * @param randomSeed If you use any randomization, use this seed for it.
	 */
	public Group1(UtilitySpace utilitySpace,
				  Map<DeadlineType, Object> deadlines,
				  Timeline timeline,
				  long randomSeed) {
		// Make sure that this constructor calls it's parent.
		super(utilitySpace, deadlines, timeline, randomSeed);
		
		party1opponentBids = new OpponentBids(utilitySpace);
	}

	
	/**
	 * Each round this method gets called and ask you to accept or offer. The first party in
	 * the first round is a bit different, it can only propose an offer.
	 *
	 * @param validActions Either a list containing both accept and offer or only offer.
	 * @return The chosen action.
	 */
	@Override
	public Action chooseAction(List<Class> validActions) {
		/*
		// with 50% chance, counter offer
		// if we are the first party, also offer.
		if (!validActions.contains(Accept.class) || Math.random() > 0.5) {
			return new Offer(generateRandomBid());
		}
		else {
			return new Accept();
		}
		*/
		//if (!validActions.contains(Accept.class)){
		
		Bid bid = this.generateRandomBid();
		return new Offer(bid);
		//}
		//else  {
			//return new Accept();
			
		//}
	}


	/**
	 * All offers proposed by the other parties will be received as a message.
	 * You can use this information to your advantage, for example to predict their utility.
	 *
	 * @param sender The party that did the action.
	 * @param action The action that party did.
	 */
	@Override
	public void receiveMessage(Object sender, Action action) {
		// Get bid
		Bid bid = Action.getBidFromAction(action);
		
		// Get bid size.
		int bidSize = bid.getIssues().size();
		
		// Create array for estimated weights if it doesn't exist.
		if (party1Weights==null){
			party1Weights = new double[bidSize];
			for(int i=0;i<bidSize;i++){
				party1Weights[i]=(double) 1/bidSize;
			} 
		}
		
		// If it does exist, update the estimated weights using frequency analysis.
		// Increment = 0.1.
		else {
			Bid lastBid = this.party1opponentBids.getOpponentsBids().get(this.party1opponentBids.getOpponentsBids().size()-1);
			
			double party1WeightSum = 0;
			for (int i=0;i<bidSize;i++){
				
				try {
					if (bid.getValue(i+1).equals(lastBid.getValue(i+1))){
						party1Weights[i]=party1Weights[i]+0.1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				party1WeightSum = party1WeightSum+party1Weights[i];
			}
			
			for(int i=0;i<bidSize;i++){
				party1Weights[i]=party1Weights[i]/party1WeightSum;
			}
		}
		System.out.println(party1Weights[0]);
		// Add current bid to bid database
		this.party1opponentBids.addBid(bid);
	}

}
