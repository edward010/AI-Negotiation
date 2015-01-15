package negotiator.group1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import agents.anac.y2010.Yushu.Utility;
import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.group1.Information.IssueValue;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.UtilitySpace;

/**
 * This is your negotiation party.
 */
public class Group1 extends AbstractNegotiationParty {

	private int count = 0;
	private Bid bid, lastBid;
	private List<Information> opponentInfo = new ArrayList<Information>();
	private List<String> opponents = new ArrayList<String>();

	private int numberOfIssues = 0;
	private int totalRounds = 0;
	private int currentRound = 0;
	
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
		
		totalRounds = (int) deadlines.get(DeadlineType.ROUND);
		System.out.println("totalRounds: " + totalRounds);
		currentRound++;
		System.out.println("agentID: " + getPartyId().toString() + "currentRound: " + currentRound);
		int roundsToGo = totalRounds - currentRound;
		//double threshold = 0.9;
		
		double currentUtility = getUtility(bid);
		
		if((double)roundsToGo < 0.1 && currentUtility > 0.6 ){
			return new Accept();
		}
		else if((double)roundsToGo < 0.2  && currentUtility > 0.6 ){
			return new Accept();
		}
		
		if ((!validActions.contains(Accept.class)||getUtility(bid)<0.9 ) && !(roundsToGo < 4 && getUtility(bid)>0.6)){
			Bid myBid = new Bid();	
			
			if((double) roundsToGo <= 0.5*(double) totalRounds){
				
				//generate bid taking others preferences into account
				
				//generate a bid with high utility for me
				try {
					myBid = Utility.getRandomBid(this.utilitySpace);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					double newUtility = 0;
					do{
						myBid = generateRandomBid();
						newUtility = getUtility(myBid);
					}
					while((newUtility < 0.75));
				}
				System.out.println("Bid generated: " + myBid.toString() + " || utility : " + getUtility(myBid));

				List<Value> importantValues = new ArrayList<Value>();
				List<Integer> importantIssues = new ArrayList<Integer>();
				numberOfIssues = opponentInfo.get(0).weights.size();
				
				for (Information oppInfo : opponentInfo){
					
					int maxWeightIndex = 0;
					System.out.println("START  oppInfo.weights.get(maxWeightIndex) : " + oppInfo.weights.get(maxWeightIndex) + " || maxweightindex is " + maxWeightIndex);
					for(int i = 1; i < numberOfIssues; i++){
						if(oppInfo.weights.get(i) > oppInfo.weights.get(maxWeightIndex)){
							maxWeightIndex = i;
						}
						System.out.println("oppInfo.weights.get(" + i + ") : " + oppInfo.weights.get(i));
						System.out.println("oppInfo.weights.get(maxWeightIndex) : " + oppInfo.weights.get(maxWeightIndex) + " || maxweightindex is " + maxWeightIndex);
					}
					//get the issue with the highest weight
					IssueValue importantIssue = oppInfo.values.get(maxWeightIndex);
					int nubmerOfValues = importantIssue.item.size();
					int mostImportant = 0;
					//get the index of the most preferred value
					for(int i = 1; i<nubmerOfValues; i++){
						System.out.println(" --- "+ i + " " +  importantIssue.item.get(i));
						if(importantIssue.number.get(i) > importantIssue.number.get(mostImportant)){
							mostImportant = i;
						}
					}
					System.out.println("mostImportant: " + mostImportant + " is "+ importantIssue.item.get(mostImportant));
					System.out.println("issueID-1 is " + maxWeightIndex);
					importantValues.add(importantIssue.item.get(mostImportant));
					importantIssues.add(maxWeightIndex);
				}
				
				//get our least important issue id-1
				System.out.println("numberOfIssues : " + numberOfIssues);
				int least = 0;
				for(int i=0; i<numberOfIssues; i++){
					if(utilitySpace.getWeight(i+1) < utilitySpace.getWeight(least+1)){
						least = i;
					}
				}
				System.out.println("least : " + least);
				
				//adjust bid for the (to us) least important issue

				for(int ii = 0; ii < importantIssues.size(); ii++){
					if(importantIssues.get(ii).equals(least)){
						try {
							System.out.println("myBid.get : " + myBid.getValue(least+1).toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						// - to be corrected
						myBid.setValue(least+1, importantValues.get(ii));
					}
					else{
						System.out.println("nope");
					}
				}
				System.out.println("Bid edit: " + myBid.toString());

			}
			else{
				//generate bids based on own utility
				
				if((double) roundsToGo > 0.75*(double) totalRounds){
					// lower utility threshold a bit
					
					double threshold = 0.8;
					double newUtility = 0;
					do{
						myBid = generateRandomBid();
						newUtility = getUtility(myBid);
					}
					while((newUtility < threshold));
				}
				else{
					//get max utility bid
					
					try {
						myBid = Utility.getRandomBid(this.utilitySpace);
					} catch (Exception e) {
						myBid = this.generateRandomBid();
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return new Offer(myBid);
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
		if (action.toString()=="(Accept)"){
			// Do nothing.
		}
		else{
			// If the message is a bid.

			// Get bid.
			bid = Action.getBidFromAction(action);
			
			// Process bid.
			processBid(sender,bid);
		}
	}

	public void addSender(String sender, Integer bidSize, Bid bid) throws Exception {
		Information info = new Information();
		List<Double> weight = new ArrayList<Double>();
		List<IssueValue> values = new ArrayList<IssueValue>();
		for(int i=0;i<bidSize;i++){
			weight.add((double) 1/bidSize);
			IssueValue value = new IssueValue();
			value.item.add(bid.getValue(i+1));
			value.number.add(1);
			values.add(value);
		}
		info.name = sender;
		info.weights = weight; 
		info.values = values;
		info.bids.add(bid);
		opponentInfo.add(info);
		opponents.add(sender);
	}

	private void processBid(Object sender, Bid bid){
		// Get bid size.
		int bidSize = bid.getIssues().size();

		// Fill the OpponentInfo
		if(!opponents.contains(sender.toString())) {
			try {
				addSender(sender.toString(), bidSize, bid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Get sender index.
		int agentIndex = opponents.indexOf(sender.toString());

		// Get last bid of sender.
		lastBid = opponentInfo.get(agentIndex).getLastBid();

		// Add current bid to bid list of sender.
		opponentInfo.get(agentIndex).bids.add(bid);

		// Create array for estimated weights and fill it with previous weights.
		double[] partyWeights = new double[bidSize];
		for (int i = 0;i<bidSize;i++){
			partyWeights[i] = opponentInfo.get(agentIndex).weights.get(i);
		}
		
		// Update the estimated weights using frequency analysis.
		// Increment = 0.01.
		double party1WeightSum = 0;
		for (int i=0;i<bidSize;i++){
			try {
				if (bid.getValue(i+1).equals(lastBid.getValue(i+1))){
					partyWeights[i]=partyWeights[i]+0.01;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Calculate sum of updated weights.
			party1WeightSum = party1WeightSum+partyWeights[i];
		}
		// Normalize the new weights with increments and set them in opponentInfo of sender.
		for(int i=0;i<bidSize;i++){
			opponentInfo.get(agentIndex).weights.set(i,partyWeights[i]/party1WeightSum);
		}
		
		// Update values.
		for (int i=0;i<bidSize;i++){
			try {
				if(!opponentInfo.get(agentIndex).values.get(i).item.contains(bid.getValue(i+1))) {
					try {
						opponentInfo.get(agentIndex).values.get(i).item.add(bid.getValue(i+1));
						opponentInfo.get(agentIndex).values.get(i).number.add(1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					int index = opponentInfo.get(agentIndex).values.get(i).item.indexOf(bid.getValue(i+1));
					opponentInfo.get(agentIndex).values.get(i).number.set(index, opponentInfo.get(agentIndex).values.get(i).number.get(index)+1);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
