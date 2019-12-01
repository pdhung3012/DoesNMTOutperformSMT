package dataExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import constanct.PathConstanct;
import utils.FileIO;
import utils.FileUtil;
import utils.SortUtil;

public class ExtractAbbreviationFromPLCorpus {

	public static String normalizeForConalaCorpus(String input) {
		return input.replaceAll("'", "").replaceAll("`", "").replaceAll(":", " ").replaceAll("\\\\", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("<unk>", "unk").replaceAll("<s>", " ").replaceAll("</s>", " ").replaceAll("\\|\\|\\|port\\|", " port ").replaceAll("[^ _a-zA-Z0-9\\-]", "").trim();
	}
	
	public static String tryGetLine(BufferedReader br) {
		String line = null;
		try {
			line = br.readLine();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}

	public static void generateAbbrevationCorpus(String fpSource, String fpOutputSource, String fpOutputTarget,int maxLine,
			HashSet<String> setVocabSource, HashSet<String> setVocabTarget) {
		StringBuilder sbResultSource = new StringBuilder();
		StringBuilder sbResultTarget = new StringBuilder();
		String[] lstInSource = FileIO.readFromLargeFile(fpSource).split("\n");
//		String[] lstInTarget=FileIO.readFromLargeFile(fpInputTarget).split("\n");

		FileIO.writeStringToFile("", fpOutputSource);
		FileIO.writeStringToFile("", fpOutputTarget);
//		System.out.println(lstInSource.length+" "+lstInTarget.length);
//		
//		setVocabSource.add(tokenUnknown);
//		setVocabTarget.add(tokenUnknown);
		System.out.println("source length " + lstInSource.length);
		int numAfterFilter = 0;
		for (int i = 0; i < lstInSource.length; i++) {
			String[] arrSource = normalizeForConalaCorpus(lstInSource[i]).trim().split("\\s+");
//			String[] arrTarget=lstInTarget[i].trim().split("\\s+");

			if (arrSource.length <= 250) {
				numAfterFilter++;
				StringBuilder sbItemSource = new StringBuilder();
				StringBuilder sbItemTarget = new StringBuilder();
				for (int j = 0; j < arrSource.length; j++) {
					String fullToken = arrSource[j].trim();
					if (!fullToken.isEmpty()) {
						String abbrevToken = arrSource[j].substring(0, 1);
						sbItemSource.append(abbrevToken + " ");
						sbItemTarget.append(fullToken + " ");
						setVocabSource.add(abbrevToken);
						setVocabTarget.add(fullToken);
					}

				}
				
				if(!sbItemSource.toString().trim().isEmpty()) {
					sbResultSource.append(sbItemSource.toString() + "\n");
					sbResultTarget.append(sbItemTarget.toString() + "\n");
				}
				
				
			
			
			}
			if ((i + 1) % 100000 == 0 || i == lstInSource.length - 1) {
				FileIO.appendStringToFile(sbResultSource.toString().trim() + "\n", fpOutputSource);
				FileIO.appendStringToFile(sbResultTarget.toString().trim() + "\n", fpOutputTarget);
				sbResultSource = new StringBuilder();
				sbResultTarget = new StringBuilder();
			}
			
			if(numAfterFilter>=maxLine) {
				break;
			}
			
		}
		
		System.out.println("after source: "+numAfterFilter);

	}

	public static int[] generateTotalAlignment(String fopPath, String fpSource, String fpTarget, String fpAlignST,
			String fpAlignTS, boolean doVerify) {
		int[] numbers = new int[] { 0, 0, 0, 0 };
		ArrayList<String> sourceSequences = FileUtil.getFileStringArray(fpSource),
				targetSequences = FileUtil.getFileStringArray(fpTarget);
		if (doVerify)
			if (sourceSequences.size() != targetSequences.size()) {
				numbers[0]++;
//				throw new AssertionError("Numbers of source and target sequences are not the same!!!");
			}
		File dir = new File(fopPath);
		if (!dir.exists())
			dir.mkdirs();
		PrintStream psS2T = null, psT2S = null;
		try {
			psS2T = new PrintStream(new FileOutputStream(fpAlignST));
			psT2S = new PrintStream(new FileOutputStream(fpAlignTS));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (psS2T != null)
				psS2T.close();
			if (psT2S != null)
				psT2S.close();
			e.printStackTrace();
			return null;
		}
		for (int i = 0; i < sourceSequences.size(); i++) {
			String source = sourceSequences.get(i), target = targetSequences.get(i);
			String[] sTokens = source.trim().split("\\s+"), tTokens = target.trim().split("\\s+");
//			if (doVerify) {
//				if (sTokens.length != tTokens.length) {
//					numbers[1]++;
////					throw new AssertionError("Lengths of source and target sequences are not the same!!!");
//				}
//				boolean aligned = true;
//				for (int j = 0; j < sTokens.length; j++) {
//					String s = sTokens[j], t = tTokens[j];
//					if ((t.contains(".") && !t.substring(t.lastIndexOf('.')+1).equals(s.substring(s.lastIndexOf('.')+1))) || (!t.contains(".") && !t.equals(s))) {
//						numbers[3]++;
//						aligned = false;
////						throw new AssertionError("Source and target are not aligned!!!");
//					}
//				}
//				if (!aligned)
//					numbers[2]++;
//			}
			String headerS2T = generateHeader(sTokens, tTokens, i), headerT2S = generateHeader(tTokens, sTokens, i);
			psS2T.println(headerS2T);
			psT2S.println(headerT2S);
			psS2T.println(target);
			psT2S.println(source);
			String alignmentS2T = generateAlignment(sTokens), alignmentT2S = generateAlignment(tTokens);
			psS2T.println(alignmentS2T);
			psT2S.println(alignmentT2S);
		}
		psS2T.flush();
		psT2S.flush();
		psS2T.close();
		psT2S.close();
		if (doVerify) {
			if (sourceSequences.size() * 3 != FileUtil.countNumberOfLines(fpAlignST)
					|| targetSequences.size() * 3 != FileUtil.countNumberOfLines(fpAlignTS))
				numbers[0]++;
		}
		return numbers;
	}

	private static String generateHeader(String[] sTokens, String[] tTokens, int i) {
		return "# sentence pair (" + i + ") source length " + sTokens.length + " target length " + tTokens.length
				+ " alignment score : 0";
	}

	private static String generateAlignment(String[] tokens) {
		StringBuilder sb = new StringBuilder();
		sb.append("NULL ({  })");
		for (int i = 0; i < tokens.length; i++) {
			String t = tokens[i];
			sb.append(" " + t + " ({ " + (i + 1) + " })");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput = PathConstanct.PATH_PL_DATA;
		String fopOutput = PathConstanct.PATH_PL_DATA + "SMT" + File.separator;
		String fopDataSMTOutput = PathConstanct.PATH_PL_DATA + "SMT" + File.separator+ "data" + File.separator;
		String fopNMTOutput = PathConstanct.PATH_PL_DATA + "NMT" + File.separator;
		String fopNMTSplitOutput = PathConstanct.PATH_PL_DATA + "NMTSplit" + File.separator;
		
		String fpFullTextTrain = fopInput + "train.t";
		String fpFullTextValid = fopInput + "tune.t";
		String fpFullTextTest = fopInput + "test.t";

		new File(fopOutput).mkdir();
		int maxLine=1000000;

		HashSet<String> setVocabSource = new LinkedHashSet<String>();
		HashSet<String> setVocabTarget = new LinkedHashSet<String>();

		generateAbbrevationCorpus(fpFullTextTrain, fopOutput + "train.s", fopOutput + "train.t",maxLine, setVocabSource,
				setVocabTarget);
		generateAbbrevationCorpus(fpFullTextValid, fopOutput + "tune.s", fopOutput + "tune.t",maxLine, setVocabSource,
				setVocabTarget);
		generateAbbrevationCorpus(fpFullTextTest, fopOutput + "test.s", fopOutput + "test.t",maxLine, setVocabSource,
				setVocabTarget);
		System.out.println("finish corpus");

		generateTotalAlignment(fopOutput, fopOutput + "train.s", fopOutput + "train.t", fopOutput + "training.s-t.A3",
				fopOutput + "training.t-s.A3", false);
		System.out.println("finish align");

		StringBuilder sbVocabSource = new StringBuilder();
		sbVocabSource.append("<unk>\n<s>\n</s>\n");
		for (String str : setVocabSource) {
			sbVocabSource.append(str + "\n");
		}
		FileIO.writeStringToFile(sbVocabSource.toString(), fopOutput + "vocab.s");

		StringBuilder sbVocabTarget = new StringBuilder();
		sbVocabTarget.append("<unk>\n<s>\n</s>\n");
		for (String str : setVocabTarget) {
			sbVocabTarget.append(str + "\n");
		}
		FileIO.writeStringToFile(sbVocabTarget.toString(), fopOutput + "vocab.t");

		System.out.println("finish vocab in SMT");
//		create data folder for SMT
		new File(fopDataSMTOutput).mkdir();
		new File(fopDataSMTOutput+"align"+File.separator).mkdir();
		new File(fopDataSMTOutput+"train"+File.separator).mkdir();
		new File(fopDataSMTOutput+"tune"+File.separator).mkdir();
		new File(fopDataSMTOutput+"test"+File.separator).mkdir();
		new File(fopDataSMTOutput+"refs"+File.separator).mkdir();
		new File(fopDataSMTOutput+"refs"+File.separator+"tune"+File.separator).mkdir();
		new File(fopDataSMTOutput+"refs"+File.separator+"test"+File.separator).mkdir();
		
		FileIO.copyFileReplaceExist(fopOutput+"training.s-t.A3", fopDataSMTOutput+"align"+File.separator+"training.s-t.A3");
		FileIO.copyFileReplaceExist(fopOutput+"training.t-s.A3", fopDataSMTOutput+"align"+File.separator+"training.t-s.A3");
		FileIO.copyFileReplaceExist(fopOutput+"train.s", fopDataSMTOutput+"train"+File.separator+"train.s");
		FileIO.copyFileReplaceExist(fopOutput+"train.t", fopDataSMTOutput+"train"+File.separator+"train.t");
		FileIO.copyFileReplaceExist(fopOutput+"tune.s", fopDataSMTOutput+"tune"+File.separator+"tune.s");
		FileIO.copyFileReplaceExist(fopOutput+"tune.t", fopDataSMTOutput+"refs"+File.separator+"tune"+File.separator+"ref0");
		FileIO.copyFileReplaceExist(fopOutput+"test.s", fopDataSMTOutput+"test"+File.separator+"test.s");
		FileIO.copyFileReplaceExist(fopOutput+"test.t", fopDataSMTOutput+"refs"+File.separator+"test"+File.separator+"ref0");
		
		
		generateNMTVocabulary(fopInput,fopNMTOutput);
		generateNMTData(fopInput, fopNMTOutput);
		generateNMTsplitLine(fopNMTOutput,fopNMTSplitOutput,50);
		
		

	}
	
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

	public static void generateNMTVocabulary(String fopInput,String fopOutput) {
		// TODO Auto-generated method stub
		
//		String fopInput=PathConstanct.fop_NL_Data+"conalaAbbrev/v3/";
		String fopTrainSource=fopInput+"train.s";
		String fopTrainTarget=fopInput+"train.t";
		String fopTuneSource=fopInput+"tune.s";
		String fopTuneTarget=fopInput+"tune.t";
		String fpMapVocabulary=fopOutput+"countVocab.txt";
		
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

	public static void generateNMTData(String fopInput,String fopOutput) {
		// TODO Auto-generated method stub
//		String fopInput=PathConstanct.fop_NL_Data+"conalaAbbrev/v3/";
//		String fopOutput=PathConstanct.fop_NL_Data+"conalaAbbrev/v3/"+"v4"+File.separator;
		String fpVocab=fopOutput+"countVocab.txt";
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
		FileIO.copyFileReplaceExist(fopInput+"test.s", fopOutput+"test.s");
		FileIO.copyFileReplaceExist(fopInput+"test.t", fopOutput+"test.t");
		
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
	
	public static void getSplitData(String fpIn,String fpOut,String fpLine,int number){
		FileIO.writeStringToFile("",fpOut);
		StringBuilder sbResult=new StringBuilder();
		StringBuilder sbLine=new StringBuilder();
		int numLine=0;

		FileIO.writeStringToFile("",fpOut);
		FileIO.writeStringToFile("",fpLine);
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpIn), StandardCharsets.ISO_8859_1)) {
		    
			for (String line = null; (line = br.readLine()) != null;) {
		    	String[] arrItems=line.trim().split("\\s+");
				numLine++;
		    	StringBuilder sbPerLine=new StringBuilder();
		    	for(int j=0;j<arrItems.length;j++){
		    		sbPerLine.append(arrItems[j]+" ");
		    		if((j+1)%number==0){
		    			sbResult.append(sbPerLine.toString().trim()+"\n");
		    			sbLine.append(numLine+"\n");
		    			sbPerLine=new StringBuilder();
		    		} else if (j==arrItems.length-1){
		    			sbResult.append(sbPerLine.toString().trim()+"\n");
		    			sbLine.append(numLine+"\tendLine"+"\n");
		    			sbPerLine=new StringBuilder();
		    		}
				}
		    	
		    	if(numLine%1000000==0){
					FileIO.appendStringToFile(sbResult.toString(),fpOut);
					FileIO.appendStringToFile(sbLine.toString(),fpLine);
					sbResult=new StringBuilder();
					sbLine=new StringBuilder();
				}
//				prevLine=line;
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		if(!sbResult.toString().isEmpty()){
			FileIO.appendStringToFile(sbResult.toString(),fpOut);
			FileIO.appendStringToFile(sbLine.toString(),fpLine);
//			sbResult=new StringBuilder();
		}
		
		
	}
	
	public static void generateNMTsplitLine(String fopIn,String fopOut,int numLine) {
		// TODO Auto-generated method stub
//		String fopIn=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"removeSparseTokens"+File.separator;
//		String fopOut=PathConstanct.PATH_PROJECT_TTT_CUR_EVAL_DATA+"removeSparseTokens_split"+File.separator;
		new File(fopOut).mkdir();
//		int numLine=50;
		getSplitData(fopIn+"train.s", fopOut+"train.s", fopOut+"train.s.line.txt",numLine);
		getSplitData(fopIn+"train.t", fopOut+"train.t", fopOut+"train.t.line.txt",numLine);
		getSplitData(fopIn+"tune.s", fopOut+"tune.s", fopOut+"tune.s.line.txt",numLine);
		getSplitData(fopIn+"tune.t", fopOut+"tune.t", fopOut+"tune.t.line.txt",numLine);
		getSplitData(fopIn+"test.s", fopOut+"test.s", fopOut+"test.s.line.txt",numLine);
		getSplitData(fopIn+"test.t", fopOut+"test.t", fopOut+"test.t.line.txt",numLine);
		
	}

}
