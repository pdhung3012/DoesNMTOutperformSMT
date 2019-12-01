package dataExtraction;

import java.util.ArrayList;
import java.util.HashMap;

import constanct.PathConstanct;
import utils.FileIO;
import utils.SortUtil;

public class AnalyseVocabularyOfInvocMap {

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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String fopInput=PathConstanct.fop_NL_Data+"conalaAbbrev/v3/";
		String fopTrainSource=fopInput+"train.s";
		String fopTrainTarget=fopInput+"train.t";
		String fopTuneSource=fopInput+"tune.s";
		String fopTuneTarget=fopInput+"tune.t";
		String fpMapVocabulary=fopInput+"countVocab.txt";
		
		HashMap<String,Integer> mapVocabulary=new HashMap<String, Integer>();
		
		collectWordsForVocabulary(fopTrainSource, mapVocabulary);
		collectWordsForVocabulary(fopTrainTarget, mapVocabulary);
		collectWordsForVocabulary(fopTuneSource, mapVocabulary);
		collectWordsForVocabulary(fopTuneTarget, mapVocabulary);
		
		mapVocabulary= SortUtil.sortHashMapStringIntByValueDesc(mapVocabulary);
		
		StringBuilder sbResult=new StringBuilder();
		for(String key:mapVocabulary.keySet()) {
			sbResult.append(key+"\t"+mapVocabulary.get(key)+"\n");
		}
		
		FileIO.writeStringToFile(sbResult.toString()+"\n", fpMapVocabulary);
		
	}

}
