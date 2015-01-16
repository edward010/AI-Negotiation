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
import negotiator.issue.Value;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.UtilitySpace;
import agents.anac.y2011.TheNegotiator.BidsCollection;
import agents.anac.y2011.IAMhaggler2011.BidCreator;
import agents.anac.y2013.MetaAgent.portfolio.IAMhaggler2012.agents2011.southampton.utils.RandomBidCreator;

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
		//System.out.println("totalRounds: " + totalRounds);
		currentRound++;
		//System.out.println("agentID: " + getPartyId().toString() + "currentRound: " + currentRound);
		int roundsToGo = totalRounds - currentRound;
		//double threshold = 0.9;
		
		double currentUtility = getUtility(bid);
		
		if((double)roundsToGo < 0.1 && currentUtility > 0.6 ){
			return new Accept();
		}
		else if((double)roundsToGo < 0.2  && currentUtility > 0.6 ){
			return new Accept();
		}
		
		if ((!validActions.contains(Accept.class)||getUtility(bid)<0.7 ) && !(roundsToGo < 4 && getUtility(bid)>0.6)){
			Bid b = new Bid();
			RandomBidCreator bc = new RandomBidCreator();
			b = bc.getBid(this.utilitySpace, 0.7, 1); 
			List<Value> value = new ArrayList<Value>();
			for(int n = 1; n <= b.getIssues().size(); n++) {
				try {
					value.add(b.getValue(n));
					//System.out.println(n + " : " + b.getValue(n));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			List<Double> utilities = new ArrayList<Double>();
			if(opponents.size() > 0) {
				double lowestUtil = 0;
				double util = 0;
				double fairUtility = 0.6;
				while (lowestUtil < fairUtility) {
					b = bc.getBid(this.utilitySpace, 0.7, 1); 
					value = new ArrayList<Value>();
					utilities = new ArrayList<Double>();
					for(int n = 1; n <= b.getIssues().size(); n++) {
						try {
							value.add(b.getValue(n));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					for(int i = 0; i < opponents.size();i++) {
						util = 0;
						double weight = 0;
						double valueNumber = 0;
						for(int x = 0; x <  b.getIssues().size();x++) {
							//define the weight of the issue
							weight = opponentInfo.get(i).weights.get(x);
							for(int y = 0; y < opponentInfo.get(i).values.get(x).item.size(); y++) {
								//define the number of the value of the issue
								if (opponentInfo.get(i).values.get(x).item.get(y).equals(value.get(x))) {
									valueNumber = opponentInfo.get(i).values.get(x).number.get(y);
									//System.out.println("SAME");
									break;
								}
								else 
									valueNumber = 1;
							}
							int max = getMaximumValueNumber(i,x);
							util = util + weight*(valueNumber/max);
						}
						utilities.add(util);
					}
					lowestUtil = lowest(utilities);
					fairUtility-=0.001;
				}
				System.out.println("util : " + lowestUtil);
				System.out.println("fairUtility : " + fairUtility);
			}
			return new Offer(b);
			//hier copy pasta

		}
		else  {
			//System.out.println("Accepted");
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
	
	public int getMaximumValueNumber(int opponentIndex, int issueNmr) {
		int max = 0;
		int temp = 0;
		for(int i = 0; i < opponentInfo.get(opponentIndex).values.get(issueNmr).number.size(); i++)
		{
			temp = opponentInfo.get(opponentIndex).values.get(issueNmr).number.get(i);
			if(max < temp) {
				max = temp;
			}
		}
		return max;
	}
	
	public double lowest(List<Double> list){  
		double lowest = 100;
		for(int n = 0; n < list.size();n++) {
			double temp = list.get(n);
			if (lowest > temp) 
				lowest = temp;
		}
		return lowest;
	}  
}
