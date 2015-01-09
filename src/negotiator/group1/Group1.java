package negotiator.group1;


import java.util.List;
import java.util.Map;

import agents.anac.y2010.Yushu.Utility;
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

	private OpponentBids party2opponentBids;
	private OpponentBids party3opponentBids;
	private OpponentBids partyopponentBids;
	private double[][] partyWeights = null;
	private String accept = "(Accept)";
	private String party2 = "Party 2";
	private int j,numberOfAgents;
	private int count = 0;
	private Bid bid;

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
		// Make sure that this constructor calls its parent.
		super(utilitySpace, deadlines, timeline, randomSeed);

		party2opponentBids = new OpponentBids(utilitySpace);
		party3opponentBids = new OpponentBids(utilitySpace);
		partyopponentBids = new OpponentBids(utilitySpace);
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
		if (!validActions.contains(Accept.class)||getUtility(bid)<0.9){
			Bid bid;
			try {
				bid = Utility.getRandomBid(this.utilitySpace);
			} catch (Exception e) {
				bid = this.generateRandomBid();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new Offer(bid);
		}
		else  {
			return new Accept();

		}
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
		
		String Senders = sender.toString();
		
		System.out.println("got message from " + Senders);

		count = count+1;
		if (count==30){
			count = count;
		}
		System.out.println(count);

		if (action.toString()==accept){
			// Do nothing for now			
		}
		else{
			// Get bid
			bid = Action.getBidFromAction(action);
			System.out.println(getUtility(bid));
			
			if (sender.toString().equals(party2)){
				j=0;
				partyopponentBids = party2opponentBids; 
				party2opponentBids.addBid(bid);
			} else {
				j=1; 
				partyopponentBids = party3opponentBids; 
				party3opponentBids.addBid(bid);
			}
			
			// Get bid size.
			int bidSize = bid.getIssues().size();
			
			// Create array for estimated weights if it doesn't exist.
			if (partyWeights==null){
				partyWeights = new double[bidSize][2];
				for(int i=0;i<bidSize;i++){
					for (int k=0;k<numberOfAgents;k++){
					partyWeights[i][k]=(double) 1/bidSize;
					}
				} 
			}
			// If it does exist, update the estimated weights using frequency analysis.
			// Increment = 0.1.
			
			else {
				Bid lastBid = this.partyopponentBids.getOpponentsBids().get(
						this.partyopponentBids.getOpponentsBids().size()-1);
				int a = this.partyopponentBids.getOpponentsBids().size();
				double party1WeightSum = 0;
				for (int i=0;i<bidSize;i++){

					try {
						if (bid.getValue(i+1).equals(lastBid.getValue(i+1))){
							partyWeights[i][j]=partyWeights[i][j]+0.1;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					party1WeightSum = party1WeightSum+partyWeights[i][j];
				}

				for(int i=0;i<bidSize;i++){
					partyWeights[i][j]=partyWeights[i][j]/party1WeightSum;
				}
			}
			//System.out.println(partyWeights[0][j]);
		}
	}

}
