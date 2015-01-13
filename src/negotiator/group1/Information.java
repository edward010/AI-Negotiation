package negotiator.group1;

import java.util.ArrayList;
import java.util.List;

import negotiator.Bid;
import negotiator.issue.Value;

public class Information {
	String name;
	List<IssueValue> values = new ArrayList<IssueValue>();
	List<Double> weights = new ArrayList<Double>();
	List<Bid> bids = new ArrayList<Bid>();
	
	public static class IssueValue {
		List<Value> item = new ArrayList<Value>();
		List<Integer> number = new ArrayList<Integer>();
	}
	
	public Bid getLastBid(){
		
		int bidSize =bids.size();
		
		Bid lastBid = bids.get(bidSize-1);
		
		return lastBid;
	}
}
