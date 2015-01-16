package negotiator.group1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agents.anac.y2010.Yushu.Utility;
import agents.anac.y2011.IAMhaggler2011.RandomBidCreator;
import agents.anac.y2012.MetaAgent.agents.MrFriendly.BidTable;
import agents.anac.y2012.MetaAgent.agents.MrFriendly.OpponentModel;
import negotiator.Agent;
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
	
	private Bid myLastBid;
	private boolean adjusted = false;
	
	private List<String> myOpponents = new ArrayList<String>();
	private HashMap<String, OpponentModel> opponentModels = new HashMap<String, OpponentModel>();
	private HashMap<String, BidTable> bidTables = new HashMap<String, BidTable>();
	private ArrayList<Issue> issueList = new ArrayList<Issue>();
	
	private RandomBidCreator rbc = new RandomBidCreator();
	
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
		issueList = this.utilitySpace.getDomain().getIssues();
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
		double threshold = 1.0;
		
		Bid myBid;
		//if my second round, set up opponent models
		/*if(currentRound == 2){
			setUpOpponentModels();
		}*/
		
		double currentUtility = getUtility(bid);
		
		if((double)roundsToGo > 0.8*totalRounds){
			threshold = 0.9;
		}
		else if ((double)roundsToGo > 0.6*totalRounds){
			threshold = 0.85;
		}
		else if ((double)roundsToGo > 0.4*totalRounds){
			threshold = 0.8;
		}
		else if ((double)roundsToGo > 0.2*totalRounds){
			threshold = 0.75;
		}
		else if (roundsToGo > 3){
			threshold = 0.7;
		}
		else{
			threshold = 0.6;
		}
		
		if(currentUtility > threshold){
			return new Accept();
		}
		else{
			//generate bid taking others preferences into account
			
			//generate a bid with high utility for me if it's my first bid
			//otherwise, take my last bid as base for my new bid
			double newUtility = 0;	
			if(myLastBid != null){
				//myBid = myLastBid;
				
				Bid tempBid;
				boolean allOpponentsUtilHigh;
				do{
					/*tempBid = generateRandomBid();
					;*/
					
					tempBid = rbc.getBid(utilitySpace, threshold-0.05, threshold+0.05);
					newUtility = getUtility(tempBid);
					allOpponentsUtilHigh = checkUtils(threshold-0.2, tempBid);
				}
				while((newUtility < threshold-0.05 && !allOpponentsUtilHigh /* && estimated opponent utility < threshold - 0.1*/ ));
				myBid = tempBid;
				System.out.println("Bid generated: " + myBid.toString() + " || utility : " + getUtility(myBid));
			}
			// first round
			else{
				try {
					myBid = Utility.getRandomBid(utilitySpace);
				} catch (Exception e1) {
					do{
						myBid = generateRandomBid();
					}
					while((newUtility < 0.9));
					e1.printStackTrace();
				}
				newUtility = getUtility(myBid);
			}
		}
		
		myLastBid = myBid;
		return new Offer(myBid);
		
		/*
		if((double)roundsToGo < 0.1 && currentUtility > 0.7 ){
			return new Accept();
		}
		else if((double)roundsToGo < 0.2  && currentUtility > 0.75 ){
			return new Accept();
		}*/
		/*
		if ((!validActions.contains(Accept.class)||getUtility(bid)<0.9 ) && !(roundsToGo < 4 && getUtility(bid)>0.6)){
			Bid myBid = new Bid();	
			
			if((double) roundsToGo <= 0.5*(double) totalRounds){
				
				//generate bid taking others preferences into account
				
				//generate a bid with high utility for me if it's my first bid
				//otherwise, take my last bid as base for my new bid
				double newUtility = 0;
				if(myLastBid != null){
					myBid = myLastBid;
				}
				// first round
				else{
					try {
						myBid = Utility.getRandomBid(utilitySpace);
					} catch (Exception e1) {
						do{
							myBid = generateRandomBid();
						}
						while((newUtility < 0.9));
						e1.printStackTrace();
					}
					newUtility = getUtility(myBid);
				}
				
				System.out.println("Bid generated: " + myBid.toString() + " || utility : " + getUtility(myBid));
				
				
				myBid = adjustLeastImportantIssue(myBid);
				if (!adjusted){
					// 
					
				}

			}
			else{
				//generate bids based on own utility
				
				if((double) roundsToGo > 0.75*(double) totalRounds){
					// lower utility threshold a bit
					
					double threshold2 = 0.8;
					double newUtility = 0;
					do{
						myBid = generateRandomBid();
						newUtility = getUtility(myBid);
					}
					while((newUtility < threshold2));
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
			myLastBid = myBid;
			return new Offer(myLastBid);
		}
		else  {
			System.out.println("Accepted");
			return new Accept();

		}
	}*/
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
		if(!myOpponents.contains(sender.toString())){
			myOpponents.add(sender.toString());
			addOpponentModel(sender.toString());
		}

		//if the opponent is seen before but has not a model yet, so the opponentModels can be adjusted
		/*if (myOpponents.contains(sender.toString()) && !opponentModels.containsKey(sender)){
			setUpOpponentModels();
		}*/
		
		// Check if the message received is a bid or an accept
		if (action.toString()=="(Accept)"){
			// Do nothing.
		}
		else if (action instanceof Offer){
			// If the message is a bid.

			// Get bid.
			bid = ((Offer)action).getBid();
			//if it's the first round, initialize issueList
			
			//if the corresponding opponentModel should exist and the current bid can be added
			if(opponentModels.containsKey(sender)){
				opponentModels.get(sender).addOpponentBid(bid);
			}
			
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
	
	//adjusts least important issue (to us) only if it is one of the most important issues of one of the opponents
	private Bid adjustLeastImportantIssue(Bid bid){
		System.out.println("Bid before edit: " + bid.toString());
		
		List<Value> importantValues = new ArrayList<Value>();
		List<Integer> importantIssues = new ArrayList<Integer>();
		numberOfIssues = opponentInfo.get(0).weights.size();
		
		for (Information oppInfo : opponentInfo){
			
			int maxWeightIndex = 0;
			//System.out.println("START  oppInfo.weights.get(maxWeightIndex) : " + oppInfo.weights.get(maxWeightIndex) + " || maxweightindex is " + maxWeightIndex);
			for(int i = 1; i < numberOfIssues; i++){
				if(oppInfo.weights.get(i) > oppInfo.weights.get(maxWeightIndex)){
					maxWeightIndex = i;
				}
				//System.out.println("oppInfo.weights.get(" + i + ") : " + oppInfo.weights.get(i));
				//System.out.println("oppInfo.weights.get(maxWeightIndex) : " + oppInfo.weights.get(maxWeightIndex) + " || maxweightindex is " + maxWeightIndex);
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
			//System.out.println("mostImportant: " + mostImportant + " is "+ importantIssue.item.get(mostImportant));
			//System.out.println("issueID-1 is " + maxWeightIndex);
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
		
		Value myVal = new Value();
		boolean sameValues = false;
		try {
			myVal = bid.getValue(least+1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//adjust bid for the (to us) least important issue
		for(int ii = 0; ii < importantIssues.size(); ii++){
			if(myVal.equals((importantValues).get(ii))){
				sameValues = true;
			}
			
			if(importantIssues.get(ii).equals(least) && !sameValues){
				try {
					System.out.println("myBid.get : " + bid.getValue(least+1).toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bid.setValue(least+1, importantValues.get(ii));
				System.out.println("yep");
				adjusted = true;
			}
			else{
				System.out.println("nope");
				adjusted = false;
			}
		}
		System.out.println("Bid after edit: " + bid.toString());
		return bid;
	}
	
	/*private void setUpOpponentModels(){
		if(issueList != null){
			for(String opponent : myOpponents){
				opponentModels.put(opponent, new OpponentModel(issueList,1,timeline));
				//bidTables.put(opponent, new BidTable(Agent, utilitySpace, threshold, opponentModels.get(opponent)));
			}
		}
	}*/
	
	private void addOpponentModel(String opponent){
		if(issueList != null && !opponentModels.containsKey(opponent)){
			opponentModels.put(opponent, new OpponentModel(issueList,1,timeline));
		}
	}

	private boolean checkUtils(double threshold, Bid bid){
		for(String opponent : opponents){
			OpponentModel oppModel = opponentModels.get(opponent);
			if(oppModel.getEstimatedUtility(bid) <= threshold){
				return false;
			}
		}
		return true;
	}
}
