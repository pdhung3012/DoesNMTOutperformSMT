package dataExtraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import analysis.TypeOfCodeTokenRegex;
import constanct.PathConstanct;
import utils.FileUtil;
import utils.ReorderingTokens;

public class SMTEvalByTypeOfToken {


//	/sensitivity_5fold_ressult\\
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fop_input=PathConstanct.PATH_EVAL_DATA+File.separator;
		String fop_output=PathConstanct.PATH_EVAL_DATA+File.separator+"eval"+File.separator;
		new File(fop_output).mkdir();
		
		String fn_trainSource="train.s";
		String fn_trainTarget="train.t";
		String fn_testSource="test.s";
		String fn_testTarget="test.t";
		String fn_testTranslation="test.tune.baseline.trans";
//		String fn_OrderTranslation="ordered.baseline.trans";
		String fn_result="result_all.txt";
		String fn_log_incorrect="log_incorrect.txt";
		String fn_log_outVocab="log_outVocab.txt";
		String fn_correctOrderTranslated="correctOrderTranslatedResult.txt";
		String fn_statisticCorrectMapping="correct_mapping.txt";
		String fn_statisticIncorrectMapping="incorrect_mappxting.txt";
//		String fn_vocabulary="vocabulary.txt";
		
		String name_map_1="map_1";
		String name_map_2_10="map_2A";
		String name_map_11_20="map_11-20";
		String name_map_21_50="map_21-50";
		String name_map_51_100="map_51-100";
		String name_map_greaterThan_100="map_greaterThan100";
		
//		HashMap<String,String> mapTotalId=MapUtil.getHashMapFromFile(fop_mapTotalId+"a_mapTotalIdAndContent.txt");
//		HashMap<String,String> mapIdLibrary=getLibraryInfo(mapTotalId);
//		System.out.println(mapTotalId.size()+" Map total ID loaded!");
		ReorderingTokens.reorderingTokens(fop_input+fn_testSource,fop_input+fn_testTarget, fop_input+fn_testTranslation, fop_input+fn_correctOrderTranslated);
		System.out.println("Finish reorder!");
		
		ArrayList<String> arrTrainSource=FileUtil.getFileStringArray(fop_input+fn_trainSource);
		ArrayList<String> arrTestSource=FileUtil.getFileStringArray(fop_input+fn_testSource);
		ArrayList<String> arrTestTarget=FileUtil.getFileStringArray(fop_input+fn_testTarget);
		ArrayList<String> arrTestTranslation=FileUtil.getFileStringArray(fop_input+fn_correctOrderTranslated);
//		ArrayList<String> arrEvaluatedTypes=FileUtil.getFileStringArray(fop_input+"evaluatedResults.txt");
//		ArrayList<String> arrVocabFromTraining=FileUtil.getFileStringArray(fop_input+fn_vocabulary);
		
//		
		HashSet<String> setVocabTrainSource=new HashSet<String>();
		HashSet<String> setVocabTrainTarget=new HashSet<String>();
		HashSet<String> setVocabTrainMapping=new HashSet<String>();
		HashMap<String,Integer> mapVocabTraining=new HashMap<String, Integer>();
		
		
		
		HashSet<String> set5Libraries=new HashSet<String>();
//		set5Libraries.add("android");
//		set5Libraries.add("com.google.gwt");
//		set5Libraries.add("com.thoughtworks.xstream");
//		set5Libraries.add("org.hibernate");
//		set5Libraries.add("org.joda.time");		
//				//set5Libraries.add("org.apache.commons.");
//		set5Libraries.add("java");
		
		set5Libraries.add(TypeOfCodeTokenRegex.MethodNameType);
		set5Libraries.add(TypeOfCodeTokenRegex.ClassNameType);
		set5Libraries.add(TypeOfCodeTokenRegex.VariableType);
		set5Libraries.add(TypeOfCodeTokenRegex.ConstanctType);
		set5Libraries.add(TypeOfCodeTokenRegex.NumericType);
		set5Libraries.add(TypeOfCodeTokenRegex.StringLiteralType);
		set5Libraries.add(TypeOfCodeTokenRegex.OtherType);
//		
		HashMap<String,PrintStream> mapCorrectPrintScreen=new HashMap<String, PrintStream>();
		HashMap<String,PrintStream> mapIncorrectPrintScreen=new HashMap<String, PrintStream>();
		
		
		for(String strKey:set5Libraries){
			try{
				PrintStream ptCorrectResult=new PrintStream(new FileOutputStream(fop_output+"cor_"+strKey+".txt"));
				PrintStream ptIncorrectResult=new PrintStream(new FileOutputStream(fop_output+"inc_"+strKey+".txt"));
				mapCorrectPrintScreen.put(strKey, ptCorrectResult);
				mapIncorrectPrintScreen.put(strKey, ptIncorrectResult);
			}catch(Exception ex){
				
			}
			
		}
		
		
		HashMap<String,HashMap<String,Integer>> mapCountPerLibrary=new HashMap<String, HashMap<String,Integer>>();
		HashMap<String,HashMap<String,Integer>> mapCountPrecisionInTraining=new HashMap<String, HashMap<String,Integer>>();
		String keyMapCountPerLibrary="total-key";
		String strCorrect="Correct";
		String strIncorrect="Incorrect";
		for(String strItem:set5Libraries){
			HashMap<String,Integer> mapElement=new HashMap<String, Integer>();
			mapElement.put("Correct", 0);
			mapElement.put("Incorrect", 0);
			mapElement.put("OOT", 0);
			mapElement.put("OOS", 0);
			mapCountPerLibrary.put(strItem, mapElement);
			
			HashMap<String,Integer> mapByNumMap=new HashMap<String, Integer>();
			
			mapByNumMap.put(name_map_1+strCorrect, 0);
			mapByNumMap.put(name_map_1+strIncorrect, 0);
			mapByNumMap.put(name_map_2_10+strCorrect, 0);
			mapByNumMap.put(name_map_2_10+strIncorrect, 0);
			mapByNumMap.put(name_map_11_20+strCorrect, 0);
			mapByNumMap.put(name_map_11_20+strIncorrect, 0);
			mapByNumMap.put(name_map_21_50+strCorrect, 0);
			mapByNumMap.put(name_map_21_50+strIncorrect, 0);
			mapByNumMap.put(name_map_51_100+strCorrect, 0);
			mapByNumMap.put(name_map_51_100+strIncorrect, 0);
			mapByNumMap.put(name_map_greaterThan_100+strCorrect, 0);
			mapByNumMap.put(name_map_greaterThan_100+strIncorrect, 0);
			
			mapCountPrecisionInTraining.put(strItem, mapByNumMap);
		}
		
		
//		set5Libraries.add("org.apache.");
//		set5Libraries.add("java.");
		
//		for(int i=0;i<arrVocabFromTraining.size();i++){
//			String[] arrMap=arrVocabFromTraining.get(i).split("\t");
//			mapVocabTraining.put(arrMap[0].trim(),Integer.parseInt (arrMap[1].trim()));
//		}
		
		
//		HashSet<Integer> lstNotReorderedLine=new HashSet<Integer>();
		
//		for(int i=0;i<arrEvaluatedTypes.size();i++){
//			String[] arrItems=arrEvaluatedTypes.get(i).split("\t");
//			//||arrTestSource.get(i).split("\\s+").length<=7
//			if(arrItems.length>=2&&(arrItems[1].equals("true"))){
//				lstNotReorderedLine.add(i+1);
//			}
//		}
		
		
		
		
//		for(int i=0;i<arrTrainSource.size();i++){
//			String[] itemTarget=arrTrainTarget.get(i).trim().split("\\s+");
//			
//			for(int j=0;j<itemSource.length;j++){
//				if(!setVocabTrainSource.contains(itemSource[j])){
//					setVocabTrainSource.add(itemSource[j]);
//				}
//				String strMap=itemSource[j]+"_"+item;
//				if(!setVocabTrainMapping.contains(itemSource[j])){
//					setVocabTrainSource.add(itemSource[j]);
//				}
//												
//			}
//									
//		}
		
	//	arrTrainSource.clear();
		ArrayList<String> arrTrainTarget=FileUtil.getFileStringArray(fop_input+fn_trainTarget);
		HashMap<String,HashSet<String>> mapCountAppearInTrain=new HashMap<String, HashSet<String>>();
		
		
		
		for(int i=0;i<arrTrainTarget.size();i++){
			String[] itemSource=arrTrainSource.get(i).trim().split("\\s+");
			
			String[] itemTarget=arrTrainTarget.get(i).trim().split("\\s+");
			for(int j=0;j<itemTarget.length;j++){
				
				//calculate library
				if(!mapCountAppearInTrain.containsKey(itemSource[j])) {
					HashSet<String> setItem=new LinkedHashSet<String>();
					setItem.add(itemTarget[j]);
					mapCountAppearInTrain.put(itemSource[j], setItem);
				} else {
					HashSet<String> setItem=mapCountAppearInTrain.get(itemSource[j]);
					setItem.add(itemTarget[j]);
				}
				
				if(!setVocabTrainTarget.contains(itemTarget[j])){
					setVocabTrainTarget.add(itemTarget[j]);
				}
				
				
				if(!setVocabTrainSource.contains(itemSource[j])){
					setVocabTrainSource.add(itemSource[j]);
				}
				String strMap=itemSource[j]+"_"+itemTarget[j];
				if(!setVocabTrainMapping.contains(strMap)){
					setVocabTrainMapping.add(strMap);
				}																								
			}
									
		}
		
		arrTrainTarget.clear();
		
		
		
		int countOutOfSource=0,countOutOfTarget=0,countAllOutOfVocab=0,countIncorrect=0,countCorrect=0;
		FileUtil.writeToFile(fop_output+fn_result, "Correct"+"\t"+"Incorrect"+"\t"+"Out_of_source"+"\t"+"Out_of_target"+"\t"+"Out_of_vocab"+"\n");
		FileUtil.writeToFile(fop_output+fn_log_incorrect, "");
		
		PrintStream ptResult=null,ptIncorrect=null,ptOutVocab=null,ptCorrect_map=null,ptIncorrect_map=null,ptCorrectLibs[]=null,ptIncorrectLibs[]=null;
		try{
			ptResult=new PrintStream(new FileOutputStream(fop_output+fn_result));
			ptIncorrect=new PrintStream(new FileOutputStream(fop_output+fn_log_incorrect));
			// ptCorrectTranslated=new PrintStream(new FileOutputStream(fop_output+fn_correctOrderTranslated));
			ptOutVocab=new PrintStream(new FileOutputStream(fop_output+fn_log_outVocab));
			ptCorrect_map=new PrintStream(new FileOutputStream(fop_output+fn_statisticCorrectMapping));
			ptIncorrect_map=new PrintStream(new FileOutputStream(fop_output+fn_statisticIncorrectMapping));
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		TypeOfCodeTokenRegex typeCodeToken=new TypeOfCodeTokenRegex();
		
		for(int i=0;i<arrTestSource.size();i++){
//			if(!lstNotReorderedLine.contains(i+1)){
//				continue;
//			}
			HashSet<String> setIncorrect=new HashSet<String>();
			HashSet<String> setOutSource=new HashSet<String>();
			HashSet<String> setOutTarget=new HashSet<String>();
			String[] itemSource=arrTestSource.get(i).trim().split("\\s+");
			String[] itemTarget=arrTestTarget.get(i).trim().split("\\s+");
			String[] itemTrans=arrTestTranslation.get(i).trim().split("\\s+");
			String strIncorrectLog="",strOutSource="",strOutTarget="";
			
			int numCSourceLine=0,numCTargetLine=0,numIncorrect=0,numCorrect=0;
			System.out.println("Line "+i);
			
			
//			if(itemSource.length>itemTrans.length) {
//				continue;
//			}
			
			for(int j=0;j<itemSource.length;j++){
				keyMapCountPerLibrary=typeCodeToken.getTypeOfToken(itemTarget[j], itemTarget, j);
						
				if(!itemTarget[j].equals(itemSource[j])){
				if(!setVocabTrainSource.contains(itemSource[j])){
						numCSourceLine++;
						int currentNumber=mapCountPerLibrary.get(keyMapCountPerLibrary).get("OOS");
						mapCountPerLibrary.get(keyMapCountPerLibrary).put("OOS",currentNumber+1);						
						if(!setOutSource.contains(itemSource[j])){
							strOutSource+=itemSource[j]+" ";
							setOutSource.add(itemSource[j]);

						}
						
					}
				//itemSource[j]+"_"+itemTarget[j]
				else if(!setVocabTrainMapping.contains(itemSource[j]+"_"+itemTarget[j])){
					//else if(!setVocabTrainTarget.contains(itemTarget[j])){
						numCTargetLine++;
						
						int currentNumber=mapCountPerLibrary.get(keyMapCountPerLibrary).get("OOT");
						mapCountPerLibrary.get(keyMapCountPerLibrary).put("OOT",currentNumber+1);
						
						
						if(!setOutTarget.contains(itemTarget[j])){
							strOutTarget+=itemTarget[j]+" ";
							setOutTarget.add(itemTarget[j]);
						}
					}else if(itemTarget[j].equals(itemTrans[j])){
						
						numCorrect++;
						
						int numAppearInTrainOfSource=mapCountAppearInTrain.get(itemSource[j]).size();						
						String itemMapBunchName="";
						if(numAppearInTrainOfSource==1) {
							itemMapBunchName=name_map_1;
						}
						else if(numAppearInTrainOfSource>=2 && numAppearInTrainOfSource<=10) {
							itemMapBunchName=name_map_2_10;
						} else if(numAppearInTrainOfSource>=11 && numAppearInTrainOfSource<=20) {
							itemMapBunchName=name_map_11_20;
						} else if(numAppearInTrainOfSource>=21 && numAppearInTrainOfSource<=50) {
							itemMapBunchName=name_map_21_50;
						} else if(numAppearInTrainOfSource>=51 && numAppearInTrainOfSource<=100) {
							itemMapBunchName=name_map_51_100;
						} else if(numAppearInTrainOfSource>100) {
							itemMapBunchName=name_map_greaterThan_100;
						}
						int curCorrectBunch=mapCountPrecisionInTraining.get(keyMapCountPerLibrary).get(itemMapBunchName+strCorrect);
						mapCountPrecisionInTraining.get(keyMapCountPerLibrary).put(itemMapBunchName+strCorrect,curCorrectBunch+1);
						
						int currentNumber=mapCountPerLibrary.get(keyMapCountPerLibrary).get("Correct");
						mapCountPerLibrary.get(keyMapCountPerLibrary).put("Correct",currentNumber+1);
						ptCorrect_map.print((i+1)+"\t"+itemSource[j]+"\t"+itemTarget[j]+"\t"+keyMapCountPerLibrary+"\n");
						mapCorrectPrintScreen.get(keyMapCountPerLibrary).print(itemSource[j]+","+mapVocabTraining.get(itemSource[j])+"\n");
					} else{
						numIncorrect++;
						
						int numAppearInTrainOfSource=mapCountAppearInTrain.get(itemSource[j]).size();						
						String itemMapBunchName="";
						if(numAppearInTrainOfSource==1) {
							itemMapBunchName=name_map_1;
						}
						else if(numAppearInTrainOfSource>=2 && numAppearInTrainOfSource<=10) {
							itemMapBunchName=name_map_2_10;
						} else if(numAppearInTrainOfSource>=11 && numAppearInTrainOfSource<=20) {
							itemMapBunchName=name_map_11_20;
						} else if(numAppearInTrainOfSource>=21 && numAppearInTrainOfSource<=50) {
							itemMapBunchName=name_map_21_50;
						} else if(numAppearInTrainOfSource>=51 && numAppearInTrainOfSource<=100) {
							itemMapBunchName=name_map_51_100;
						} else if(numAppearInTrainOfSource>100) {
							itemMapBunchName=name_map_greaterThan_100;
						}
						int curIncorrectBunch=mapCountPrecisionInTraining.get(keyMapCountPerLibrary).get(itemMapBunchName+strIncorrect);
						mapCountPrecisionInTraining.get(keyMapCountPerLibrary).put(itemMapBunchName+strIncorrect,curIncorrectBunch+1);

						
						int currentNumber=mapCountPerLibrary.get(keyMapCountPerLibrary).get("Incorrect");
						mapCountPerLibrary.get(keyMapCountPerLibrary).put("Incorrect",currentNumber+1);
					//	if(!setIncorrect.contains(itemTrans[j]+"(Correct: "+itemTarget[j]+") ")){
							strIncorrectLog+=itemTrans[j]+"(Correct: "+itemTarget[j]+") ";
							setIncorrect.add(itemTrans[j]+"(Correct: "+itemTarget[j]+") ");
						//}
//							if(itemSource[j].equals("OnPreDrawListener()")){
//								System.out.println("line "+i);
//								Scanner sc=new Scanner(System.in);
//								sc.next();
//							}
							//
							ptIncorrect_map.print((i+1)+"\t"+itemSource[j]+"\t"+itemTrans[j]+"\t"+itemTarget[j]+"\n");
							mapIncorrectPrintScreen.get(keyMapCountPerLibrary).print(itemSource[j]+","+mapVocabTraining.get(itemSource[j])+","+itemTarget[j]+"\n");
					}
				}
												
			}
			countCorrect+=numCorrect;
			countIncorrect+=numIncorrect;
			countOutOfSource+=numCSourceLine;
			countOutOfTarget+=numCTargetLine;
			countAllOutOfVocab+=numCSourceLine+numCTargetLine;
			ptResult.print("Line "+(i+1)+" (correct/incorrect/OOS/OOT/OOV): "+numCorrect+"\t"+numIncorrect+"\t"+numCSourceLine+"\t"+numCTargetLine+"\t"+(numCSourceLine+numCTargetLine)+"\n");
			ptIncorrect.print("Line "+(i+1)+" : "+strIncorrectLog+"\n");
			ptOutVocab.print("Line "+(i+1)+" : "+strOutSource.trim()+" ||| "+strOutTarget.trim()+"\n");
			//FileUtil.appendToFile(fop_input+fn_result, numCorrect+"\t"+numIncorrect+"\t"+numCSourceLine+"\t"+numCTargetLine+"\t"+(numCSourceLine+numCTargetLine)+"\n");
	//		FileUtil.appendToFile(fop_input+fn_log_incorrect, strIncorrectLog+"\n");
			
		}
		
		try{
			ptResult.close();
			ptIncorrect.close();
		//	ptCorrectTranslated.close();
			ptOutVocab.close();
			ptCorrect_map.close();
			ptIncorrect_map.close();
			
		}catch(Exception ex){
			
		}
		double precision=countCorrect*1.0/(countCorrect+countIncorrect);
		double recall=countCorrect*1.0/(countCorrect+countAllOutOfVocab);
		double f1score=precision*recall*2/(precision+recall);
		
//		FileUtil.appendToFile(fop_output+fn_result, "Precision: "+countCorrect*1.0/(countCorrect+countIncorrect)+"\n");
//		FileUtil.appendToFile(fop_output+fn_result, "Recall: "+countCorrect*1.0/(countCorrect+countAllOutOfVocab)+"\n");
		FileUtil.appendToFile(fop_output+fn_result, "Total:\t"+countCorrect+"\t"+countIncorrect+"\t"+countOutOfSource+"\t"+countOutOfTarget+"\t"+countAllOutOfVocab+"\t"+precision+"\t"+recall+"\t"+f1score+"\n");
		
		for(String strItem:mapCountPerLibrary.keySet()){
			HashMap<String,Integer> mapTemp=mapCountPerLibrary.get(strItem);
			precision=mapTemp.get("Correct")*1.0/(mapTemp.get("Correct")+mapTemp.get("Incorrect"));
			recall=mapTemp.get("Correct")*1.0/(mapTemp.get("Correct")+(mapTemp.get("OOS")+mapTemp.get("OOT")));
			f1score=precision*recall*2/(precision+recall);
			FileUtil.appendToFile(fop_output+fn_result, strItem+":\t"+mapTemp.get("Correct")+"\t"+mapTemp.get("Incorrect")+"\t"+mapTemp.get("OOS")+"\t"+mapTemp.get("OOT")+"\t"+(mapTemp.get("OOS")+mapTemp.get("OOT"))+"\t"+precision+"\t"+recall+"\t"+f1score+"\n");
		}
		
		FileUtil.appendToFile(fop_output+fn_result,"Precision per mapping number\n");
		//print percentage per library
		
		int num1=0,num2_10=0,num11_20=0,num21_50=0,num51_100=0,num_101=0;
		
		for(String strItem:mapCountPrecisionInTraining.keySet()) {
			
			String strContent=strItem+":\t";
			int numberOfCasePerLib=0;
			
			HashMap<String,Integer> mapTemp=mapCountPrecisionInTraining.get(strItem);
			int correctNum=mapTemp.get(name_map_1+strCorrect);
			int incNum=mapTemp.get(name_map_1+strIncorrect);
			numberOfCasePerLib+=correctNum+incNum;
			precision=((correctNum+incNum)!=0)?(correctNum*1.0/(correctNum+incNum)):0;
			strContent+=precision+"\t";
			num1+=correctNum+incNum;
			
		//	System.out.println(mapTemp.toString());
//			HashMap<String,Integer> mapTemp2=mapCountPrecisionInTraining.get(strItem);
//			System.out.println(name_map_2_10+"  "+mapTemp2.keySet().toString());
			correctNum=mapTemp.get(name_map_2_10+strCorrect);
			incNum=mapTemp.get(name_map_2_10+strIncorrect);
			precision=((correctNum+incNum)!=0)?(correctNum*1.0/(correctNum+incNum)):0;
			numberOfCasePerLib+=correctNum+incNum;
			strContent+=precision+"\t";
			num2_10+=correctNum+incNum;
			
			correctNum=mapTemp.get(name_map_11_20+strCorrect);
			incNum=mapTemp.get(name_map_11_20+strIncorrect);
			precision=((correctNum+incNum)!=0)?(correctNum*1.0/(correctNum+incNum)):0;
			numberOfCasePerLib+=correctNum+incNum;
			strContent+=precision+"\t";
			num11_20+=correctNum+incNum;
			
			correctNum=mapTemp.get(name_map_21_50+strCorrect);
			incNum=mapTemp.get(name_map_21_50+strIncorrect);
			precision=((correctNum+incNum)!=0)?(correctNum*1.0/(correctNum+incNum)):0;
			numberOfCasePerLib+=correctNum+incNum;
			strContent+=precision+"\t";
			num21_50+=correctNum+incNum;
			
			correctNum=mapTemp.get(name_map_51_100+strCorrect);
			incNum=mapTemp.get(name_map_51_100+strIncorrect);
			precision=((correctNum+incNum)!=0)?(correctNum*1.0/(correctNum+incNum)):0;
			numberOfCasePerLib+=correctNum+incNum;
			strContent+=precision+"\t";
			num51_100+=correctNum+incNum;
			
			correctNum=mapTemp.get(name_map_greaterThan_100+strCorrect);
			incNum=mapTemp.get(name_map_greaterThan_100+strIncorrect);
			precision=((correctNum+incNum)!=0)?(correctNum*1.0/(correctNum+incNum)):0;
			numberOfCasePerLib+=correctNum+incNum;
			strContent+=precision+"\t";
			num_101+=correctNum+incNum;
			
//			strContent+=numberOfCasePerLib+"\n";
			FileUtil.appendToFile(fop_output+fn_result, strContent+"\n");

			
		}
		
		int total=num1+num2_10+num11_20+num21_50+num51_100+num_101;
		
		FileUtil.appendToFile(fop_output+fn_result,"Total:\t"+num1+"\t"+num2_10+"\t"+num11_20+"\t"+num21_50+"\t"+num51_100+"\t"+num_101+"\n");
		
		FileUtil.appendToFile(fop_output+fn_result,"Percentage:\t"+num1*1.0/total+"\t"+num2_10*1.0/total+"\t"+num11_20*1.0/total+"\t"+num21_50*1.0/total+"\t"+num51_100*1.0/total+"\t"+num_101*1.0/total+"\n");
		
		
	}


}
