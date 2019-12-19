package dataExtraction;

import java.util.HashMap;

import constanct.PathConstanct;
import utils.FileIO;
import utils.SortUtil;

public class AnalyseTuneData {

	public static void collectWordsForVocabulary(String fpText,HashMap<String,Integer> mapVocabulary) {
		String[] lstLines=FileIO.readFromLargeFile(fpText).split("\n");
		for(int i=0;i<lstLines.length;i++) {
			String strItem=lstLines[i].trim();
			String[] arrTokens=strItem.split("\\s+");
			for (int j=0;j<arrTokens.length;j++) {
				String token=arrTokens[j];
				if(!mapVocabulary.containsKey(token)) {
					mapVocabulary.put(token, 1);
				} else {
					int number=mapVocabulary.get(token)+1;
					mapVocabulary.put(token, number);
				}				
			}
			
		}
	}
	
	public static void calculateForVocabulary(String fpText,HashMap<String,Integer> mapVocabulary,HashMap<String,Integer> mapTune) {
		String[] lstLines=FileIO.readFromLargeFile(fpText).split("\n");
		for(int i=0;i<lstLines.length;i++) {
			String strItem=lstLines[i].trim();
			String[] arrTokens=strItem.split("\\s+");
			for (int j=0;j<arrTokens.length;j++) {
				String token=arrTokens[j];
				if(!mapVocabulary.containsKey(token)) {
					mapTune.put(token, 0);
				} else {
					int number=mapVocabulary.get(token)+1;
					mapTune.put(token, number);
				}				
			}
			
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String fopInput=PathConstanct.PATH_SN_GROUP;
		String fopTrainSource=fopInput+"train.s";
		String fopTrainTarget=fopInput+"train.t";
		String fopTuneSource=fopInput+"tune.s";
		String fopTuneTarget=fopInput+"tune.t";
		String fpMapVocabulary=fopInput+"tune_vocabs.txt";
		
		HashMap<String,Integer> mapVocabulary=new HashMap<String, Integer>();
		HashMap<String,Integer> mapTune=new HashMap<String, Integer>();
		
		collectWordsForVocabulary(fopTrainSource, mapVocabulary);
		collectWordsForVocabulary(fopTrainTarget, mapVocabulary);
		
		calculateForVocabulary(fopTuneSource, mapVocabulary,mapTune);
		calculateForVocabulary(fopTuneTarget, mapVocabulary,mapTune);
		
		mapTune= SortUtil.sortHashMapStringIntByValueDesc(mapTune);
		
		StringBuilder sbResult=new StringBuilder();
		for(String key:mapTune.keySet()) {
			sbResult.append(key+"\t"+mapTune.get(key)+"\n");
		}
		
		FileIO.writeStringToFile(sbResult.toString()+"\n", fpMapVocabulary);
		
	}

}
