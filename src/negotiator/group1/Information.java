package negotiator.group1;

import java.util.ArrayList;
import java.util.List;

import negotiator.Bid;

public class Information {
	String name;
	List<Value> values = new ArrayList<Value>();
	List<Double> weights = new ArrayList<Double>();
	List<Bid> bids = new ArrayList<Bid>();
	
	public static class Value {
		List<String> item = new ArrayList<String>();
		List<Integer> number = new ArrayList<Integer>();
	}
	
	public Bid getLastBid(){
		
		int bidSize =bids.size();
		
		Bid lastBid = bids.get(bidSize-1);
		
		return lastBid;
	}
}
