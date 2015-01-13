package negotiator.group1;

import java.util.ArrayList;
import java.util.List;

public class Information {
	String name;
	List<Value> values = new ArrayList<Value>();
	List<Double> weights = new ArrayList<Double>();
	
	public static class Value {
		List<String> item = new ArrayList<String>();
		List<Integer> number = new ArrayList<Integer>();
	}
}
