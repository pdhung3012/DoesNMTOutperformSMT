package dataExtraction;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import constanct.PathConstanct;
import utils.FileIO;

public class CreateTrainingData {

//	public static String replaceTargetWithTotalId(String target,HashMap<String,String> mapReplaceId){
//		StringBuilder strResult=new StringBuilder();
//		String[] arrOldTarget=target.split("\n");
//		for(int i=0;i<arrOldTarget.length;i++){
//			String[] arrItem=arrOldTarget[i].trim().split("\\s+");
//			StringBuilder line=new StringBuilder();
//			for(int j=0;j<arrItem.length;j++){
//				if(arrItem[j].startsWith("E-") ){
//					String totalId=mapReplaceId.get(arrItem[j]);
//					if(totalId!=null){
//						line.append(totalId+" ");
//					} else{
//						line.append(arrItem[j]+" ");
//					}
////					line.append(arrItem[j]+" ");
//					
//				} else{
//					line.append(arrItem[j]+" ");
//				}
//			}			
//			strResult.append(line.toString().trim()+"\n");
//		}
//		return strResult.toString();
//	}
	
	public static String replaceTargetWithTotalId(String target,HashMap<String,String> mapReplaceId,String fpTempWrite){
		StringBuilder strResult=new StringBuilder();
		String[] arrOldTarget=target.split("\n");
		FileIO.writeStringToFile("", fpTempWrite);
		for(int i=0;i<arrOldTarget.length;i++){
			String[] arrItem=arrOldTarget[i].trim().split("\\s+");
			StringBuilder line=new StringBuilder();
			for(int j=0;j<arrItem.length;j++){
				if(arrItem[j].startsWith("E-") ){
					String totalId=mapReplaceId.get(arrItem[j]);
					if(totalId!=null){
						line.append(totalId+" ");
					} else{
						line.append(arrItem[j]+" ");
					}
//					line.append(arrItem[j]+" ");
					
				} else{
					line.append(arrItem[j]+" ");
				}
			}
			//System.out.println(line);
			strResult.append(line.toString().trim()+"\n");
			if((i+1)%100000==0||i+1==arrOldTarget.length){
				FileIO.appendStringToFile(strResult.toString(), fpTempWrite);
				strResult=new StringBuilder();
			}
		}
		String str=FileIO.readStringFromFile(fpTempWrite);
		return str;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFolder = PathConstanct.PATH_OUTPUT_ABBREV_PROJECT;
		String outputFolder = PathConstanct.PATH_OUTPUT_ABBREV_CODE;

		File fInFolder = new File(inputFolder);

		File[] arrIn = fInFolder.listFiles();
		
		FileIO.writeStringToFile("", outputFolder+"target.txt");
		FileIO.writeStringToFile("", outputFolder+"source.txt");
		FileIO.writeStringToFile("", outputFolder+"locations.txt");
		
		for (int i = 0; i < arrIn.length; i++) {
			if (arrIn[i].isDirectory()) {
				String fpLocation=arrIn[i].getAbsolutePath()
						+ File.separator+"locations.txt";
				String fpSource=arrIn[i].getAbsolutePath()
						+ File.separator+"source.txt";
				String fpTarget=arrIn[i].getAbsolutePath()
						+ File.separator+"target.txt";
				String fpMapIdenAndId = arrIn[i].getAbsolutePath()
						+ File.separator + "hash" + File.separator
						+ "mapIdenAndId.txt";
				
				String strTarget=FileIO.readStringFromFile(fpTarget);
//				String strNewTarget=replaceTargetWithTotalId(strTarget, mapReplaceId);
				
//				String strNewTarget=strTarget;
				FileIO.appendStringToFile(strTarget, outputFolder+"target.txt");
				String strSource=FileIO.readStringFromFile(fpSource);
				FileIO.appendStringToFile(strSource, outputFolder+"source.txt");
				String strLocation=FileIO.readStringFromFile(fpLocation);
				FileIO.appendStringToFile(strLocation, outputFolder+"locations.txt");
				System.out.println(i + " finish " + arrIn[i].getName()
						+ " size " );
			}
		}
	}
}
