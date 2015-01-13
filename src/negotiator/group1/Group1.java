package negotiator.group1;


import java.io.Console;
import java.util.ArrayList;
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
import negotiator.group1.Information.Value;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.UtilitySpace;

/**
 * This is your negotiation party.
 */
public class Group1 extends AbstractNegotiationParty {

	private OpponentBids party2opponentBids;
	private OpponentBids party3opponentBids;
	private double[][] partyWeights = null;
	private String accept = "(Accept)";
	private String party2 = "Party 2";
	private int j, numberOfAgents;
	private int count = 0;
	private Bid bid, lastBid;
	private List<Information> opponentInfo = new ArrayList<Information>();
	private List<String> opponents = new ArrayList<String>();

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
				e.printStackTrace();
			}
			return new Offer(bid);
		}
		else  {
			System.out.println("Accepted");
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
		// Debug purpose.
		count++;
		if (count==170){
			count = count;
		}


		// Check if the message received is a bid or an accept
		if (action.toString()==accept){
			// Save bid as being acceptable bid for that agent.
			if (sender.toString().equals(party2)){
				
			} else {
				
			}
		}
		else{
			// If the message is a bid, estimate weights for that agent.
			
			// Get bid.
			bid = Action.getBidFromAction(action);

			
			// Check which agent sent the message.
			// Also fill the lastBid variable with the previous bid. If none exists, take current bid as previous bid.
			if (sender.toString().equals(party2)){
				this.j=0;
				if (this.party2opponentBids.getOpponentsBids().size()>1){
				lastBid = this.party2opponentBids.getOpponentsBids().get(
						this.party2opponentBids.getOpponentsBids().size()-1); }
				else {
					lastBid = bid;
				}
				
				party2opponentBids.addBid(bid);
			} else {
				this.j=1;
				if (this.party3opponentBids.getOpponentsBids().size()>1){
					lastBid = this.party3opponentBids.getOpponentsBids().get(
						this.party3opponentBids.getOpponentsBids().size()-1);  }
				else {
					lastBid = bid;
				}
				party3opponentBids.addBid(bid);
			}

			// Get bid size.
			int bidSize = bid.getIssues().size();
			numberOfAgents = 2;
			
			// Fill the OpponentInfo
			if(!opponents.contains(sender.toString())) {
				try {
					addSender(sender.toString(), bidSize, bid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

			// Create array for estimated weights if it doesn't exist.
			if (partyWeights==null){
				partyWeights = new double[bidSize][numberOfAgents];
				for(int i=0;i<bidSize;i++){
					for (int k=0;k<numberOfAgents;k++){
						partyWeights[i][k]=(double) 1/bidSize;
					}
				} 
			}
			
			// If it does exist, update the estimated weights using frequency analysis.
			// Increment = 0.01.
			else {
				double party1WeightSum = 0;
				for (int i=0;i<bidSize;i++){
					try {
						if (bid.getValue(i+1).equals(lastBid.getValue(i+1))){
							partyWeights[i][this.j]=partyWeights[i][this.j]+0.01;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					party1WeightSum = party1WeightSum+partyWeights[i][this.j];
				}
				// Normalize the new weights with increments.
				for(int i=0;i<bidSize;i++){
					partyWeights[i][this.j]=partyWeights[i][this.j]/party1WeightSum;
				}
			}
		}
	}
	
	public void addSender(String sender, Integer bidSize, Bid bid) throws Exception {
		Information info = new Information();
		List<Double> weight = new ArrayList<Double>();
		List<Value> values = new ArrayList<Value>();
		for(int i=0;i<bidSize;i++){
				weight.add((double) 1/bidSize);
				Value value = new Value();
				value.item.add(bid.getValue(i+1).toString());
				value.number.add(1);
				values.add(value);
			}
		info.name = sender;
		info.weights = weight; 
		info.values = values;
		opponentInfo.add(info);
		opponents.add(sender);
	}

}
