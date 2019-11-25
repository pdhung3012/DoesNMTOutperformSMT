package utils;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MapUtil {

	public static String tryGetLine(BufferedReader br) {
		String line=null;
		try {
			line = br.readLine();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}
	
	public static void saveToFile(HashMap<String,Integer> map,String fpOutFile){
		try{
			StringBuilder sb=new StringBuilder();
			for(String str:map.keySet()){
				sb.append(str+"\t"+map.get(str)+"\n");
			}
			FileIO.writeStringToFile(sb.toString()+"\n", fpOutFile);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static HashMap<String,String> getHashMapFromFile(String fp){
		HashMap<String,String> map=new LinkedHashMap<String, String>();
		String[] arr=FileIO.readStringFromFile(fp).split("\n");
		for(int i=0;i<arr.length;i++) {
			String[] arrItems=arr[i].split("\t");
	    	if(arrItems.length>=2){
	    		map.put(arrItems[0].trim(), arrItems[1].trim());
	    	}
		}
		/*
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fp), StandardCharsets.US_ASCII)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
		    	String[] arrItems=line.split("\t");
		    	if(arrItems.length>=2){
		    		map.put(arrItems[0].trim(), arrItems[1].trim());
		    	}
		    	
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		*/
		return map;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
