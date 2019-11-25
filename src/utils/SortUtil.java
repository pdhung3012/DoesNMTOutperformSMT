package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SortUtil {

	public static HashMap<String, Integer> sortHashMapStringIntByValueDesc(HashMap<String, Integer> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(
				hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
	
	public static HashMap<String, Double> sortHashMapStringDoubleByValueDesc(HashMap<String, Double> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
				hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
	
	public static HashMap<String, HashSet<String>> sortHashMapStringHSStringByValueDesc(HashMap<String, HashSet<String>> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, HashSet<String>>> list = new LinkedList<Map.Entry<String, HashSet<String>>>(
				hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, HashSet<String>>>() {
			public int compare(Map.Entry<String, HashSet<String>> o1,
					Map.Entry<String, HashSet<String>> o2) {
				return (o2.getValue().size()-o1.getValue().size());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, HashSet<String>> temp = new LinkedHashMap<String, HashSet<String>>();
		for (Map.Entry<String, HashSet<String>> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
	
	public static HashMap<String, Integer> getOrderInHM(HashMap<String, Integer> hIn){
		HashMap<String, Integer> hOut =new LinkedHashMap<>();
		int index=0;
		for(String str:hIn.keySet()){
			index++;
			hOut.put(str, index);
		}
		return hOut;
	}
	
	
	
	public static HashMap<String,HashSet<String>> sortHashMapStringStringSetByValueDesc(HashMap<String, HashSet<String>> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, HashSet<String>>> list = new LinkedList<Map.Entry<String, HashSet<String>>>(
				hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, HashSet<String>>>() {
			public int compare(Map.Entry<String, HashSet<String>> o1,
					Map.Entry<String, HashSet<String>> o2) {
				return o2.getValue().size()-o1.getValue().size();
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, HashSet<String>> temp = new LinkedHashMap<String, HashSet<String>>();
		for (Map.Entry<String, HashSet<String>> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
	
	public static HashMap<String,HashSet<Integer>> sortHashMapStringIntSetByValueDesc(HashMap<String, HashSet<Integer>> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, HashSet<Integer>>> list = new LinkedList<Map.Entry<String, HashSet<Integer>>>(
				hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, HashSet<Integer>>>() {
			public int compare(Map.Entry<String, HashSet<Integer>> o1,
					Map.Entry<String, HashSet<Integer>> o2) {
				return o2.getValue().size()-o1.getValue().size();
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, HashSet<Integer>> temp = new LinkedHashMap<String, HashSet<Integer>>();
		for (Map.Entry<String, HashSet<Integer>> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
