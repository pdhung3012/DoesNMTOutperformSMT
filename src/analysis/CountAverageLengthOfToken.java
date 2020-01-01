package analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import constanct.PathConstanct;
import utils.FileIO;
import utils.SortUtil;

public class CountAverageLengthOfToken {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.PATH_ANA_AVERAGE_LENGTH;
		String fpTrainSource=fopInput+"train.s";
		String fpTrainTarget=fopInput+"train.t";
		String fpTuneSource=fopInput+"tune.s";
		String fpTuneTarget=fopInput+"tune.t";
		String fpTestSource=fopInput+"test.s";
		String fpTestTarget=fopInput+"test.t";
		String fpOrderVocab=fopInput+"lengthVocab.txt";
		
		String[] arrTrainSource=FileIO.readFromLargeFile(fpTrainSource).split("\n");
		String[] arrTrainTarget=FileIO.readFromLargeFile(fpTrainTarget).split("\n");
		String[] arrTuneSource=FileIO.readFromLargeFile(fpTuneSource).split("\n");
		String[] arrTuneTarget=FileIO.readFromLargeFile(fpTuneTarget).split("\n");
		String[] arrTestSource=FileIO.readFromLargeFile(fpTestSource).split("\n");
		String[] arrTestTarget=FileIO.readFromLargeFile(fpTestTarget).split("\n");
		
		long lengthToken=0,numToken=0;
		HashSet<String> setTokens=new HashSet<String>(); 
		
//		for(int i=0;i<arrTrainSource.length;i++) {
//			String[] arrItems=arrTrainSource[i].split("\\s+");
//			for(int j=0;j<arrItems.length;j++) {
//				if(!arrItems[j].isEmpty()) {
//					lengthToken+=arrItems[j].length();
//					numToken+=1;
//				}
//			}
//		}
		
		for(int i=0;i<arrTrainTarget.length;i++) {
			String[] arrItems=arrTrainTarget[i].split("\\s+");
			for(int j=0;j<arrItems.length;j++) {
				if(!arrItems[j].isEmpty()) {
					setTokens.add(arrItems[j]);
//					lengthToken+=arrItems[j].length();
//					numToken+=1;
				}
			}
		}
		
//		for(int i=0;i<arrTuneSource.length;i++) {
//			String[] arrItems=arrTuneSource[i].split("\\s+");
//			for(int j=0;j<arrItems.length;j++) {
//				if(!arrItems[j].isEmpty()) {
//					lengthToken+=arrItems[j].length();
//					numToken+=1;
//				}
//			}
//		}
		
		for(int i=0;i<arrTuneTarget.length;i++) {
			String[] arrItems=arrTuneTarget[i].split("\\s+");
			for(int j=0;j<arrItems.length;j++) {
				if(!arrItems[j].isEmpty()) {
//					lengthToken+=arrItems[j].length();
//					numToken+=1;
					setTokens.add(arrItems[j]);
				}
			}
		}
		
//		for(int i=0;i<arrTestSource.length;i++) {
//			String[] arrItems=arrTestSource[i].split("\\s+");
//			for(int j=0;j<arrItems.length;j++) {
//				if(!arrItems[j].isEmpty()) {
//					lengthToken+=arrItems[j].length();
//					numToken+=1;
//				}
//			}
//		}

		
		for(int i=0;i<arrTestTarget.length;i++) {
			String[] arrItems=arrTestTarget[i].split("\\s+");
			for(int j=0;j<arrItems.length;j++) {
				if(!arrItems[j].isEmpty()) {
					setTokens.add(arrItems[j]);
//					lengthToken+=arrItems[j].length();
//					numToken+=1;
				}
			}
		}
		
		for(String key:setTokens) {
			numToken+=1;
			lengthToken+=key.length();
		}
		
		Comparator<String> compareByLength = new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {
		        return o1.length()-o2.length();
		    }
		};
		
		List<String> list = new ArrayList<String>(setTokens);
		Collections.sort(list,compareByLength);
		
		StringBuilder sbResult=new StringBuilder();
		for(String key:list) {
			sbResult.append(key+"\t"+key.length()+"\n");
		}
		FileIO.writeStringToFile(sbResult.toString(), fpOrderVocab);
		
		double average=(lengthToken*1.0)/numToken;
		System.out.println(numToken+"\t"+average);
		
		
		
		
	}

}
