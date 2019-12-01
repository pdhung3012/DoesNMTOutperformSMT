package dataExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import constanct.PathConstanct;
import utils.FileIO;

public class GenerateCorpusNMTRemoveSparseToken {
	
	public static String tryGetLine(BufferedReader br) {
		String line=null;
		try {
			line = br.readLine();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}
	
	public static void removeSparseTokens(String fpInputSource,String fpOutputSource,String fpInputTarget,String fpOutputTarget,HashMap<String,Integer> mapVocabMoreTokens,HashSet<String> setVocabSource,HashSet<String> setVocabTarget){
		StringBuilder sbResultSource=new StringBuilder();
		StringBuilder sbResultTarget=new StringBuilder();
		String[] lstInSource=FileIO.readFromLargeFile(fpInputSource).split("\n");
		String[] lstInTarget=FileIO.readFromLargeFile(fpInputTarget).split("\n");
		String tokenUnknown="Unknown";
		
		FileIO.writeStringToFile("",fpOutputSource);
		FileIO.writeStringToFile("",fpOutputTarget);
		System.out.println(lstInSource.length+" "+lstInTarget.length);
		
		setVocabSource.add(tokenUnknown);
		setVocabTarget.add(tokenUnknown);
		
		for(int i=0;i<lstInSource.length;i++) {
			String[] arrSource=lstInSource[i].trim().split("\\s+");
			String[] arrTarget=lstInTarget[i].trim().split("\\s+");
			StringBuilder sbItemSource=new StringBuilder();
			StringBuilder sbItemTarget=new StringBuilder();
			for(int j=0;j<arrSource.length;j++) {
				if(mapVocabMoreTokens.containsKey(arrSource[j]) && mapVocabMoreTokens.containsKey(arrTarget[j]) ) {
					sbItemSource.append(arrSource[j]+" ");
					sbItemTarget.append(arrTarget[j]+" ");
					if(!setVocabSource.contains(arrSource[j])) {
						setVocabSource.add(arrSource[j]);
					}
					if(!setVocabTarget.contains(arrTarget[j])) {
						setVocabTarget.add(arrTarget[j]);
					}
				} else {
					sbItemSource.append(tokenUnknown+" ");
					sbItemTarget.append(tokenUnknown+" ");
				}
			}
			
			sbResultSource.append(sbItemSource.toString()+"\n");
			sbResultTarget.append(sbItemTarget.toString()+"\n");
			
			if((i+1)%100000 ==0 || i==lstInSource.length-1){
				FileIO.appendStringToFile(sbResultSource.toString().trim()+"\n",fpOutputSource);
				FileIO.appendStringToFile(sbResultTarget.toString().trim()+"\n",fpOutputTarget);
				sbResultSource=new StringBuilder();
				sbResultTarget=new StringBuilder();
			}
		}
		
		
				
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.fop_NL_Data+"conalaAbbrev/v3/";
		String fopOutput=PathConstanct.fop_NL_Data+"conalaAbbrev/v3/"+"v4"+File.separator;
		String fpVocab=fopInput+"countVocab.txt";
		int numAppearInCorpus=10;
		
		new File(fopOutput).mkdir();
		String[] arrVocabs=FileIO.readStringFromFile(fpVocab).split("\n");
		HashMap<String,Integer> mapVocabs=new HashMap<String, Integer>();
		for(int i=0;i<arrVocabs.length;i++) {
			String[] itemVocab=arrVocabs[i].split("\t");
			if(itemVocab.length>=2) {
				int numItem=Integer.parseInt(itemVocab[1]);
				if((!itemVocab[0].isEmpty()) && numItem>=numAppearInCorpus){
					mapVocabs.put(itemVocab[0], numItem);
					
				}				
			}
		}
		
		HashSet<String> setVocabSource=new HashSet<String>();
		HashSet<String> setVocabTarget=new HashSet<String>();
		
		
		removeSparseTokens(fopInput+"train.s",fopOutput+"train.s",fopInput+"train.t",fopOutput+"train.t",mapVocabs,setVocabSource,setVocabTarget);
		removeSparseTokens(fopInput+"tune.s",fopOutput+"tune.s",fopInput+"tune.t",fopOutput+"tune.t",mapVocabs,setVocabSource,setVocabTarget);
		
		StringBuilder sbVocabSource=new StringBuilder();
		sbVocabSource.append("<unk>\n<s>\n</s>\n");
		for(String str:setVocabSource) {
			sbVocabSource.append(str+"\n");
		}
		FileIO.writeStringToFile(sbVocabSource.toString(), fopOutput+"vocab.s");

		
		StringBuilder sbVocabTarget=new StringBuilder();
		sbVocabTarget.append("<unk>\n<s>\n</s>\n");
		for(String str:setVocabTarget) {
			sbVocabTarget.append(str+"\n");
		}
		FileIO.writeStringToFile(sbVocabTarget.toString(), fopOutput+"vocab.t");
		
		
		
	}

}
