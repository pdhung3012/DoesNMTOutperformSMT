package analysis;

import constanct.PathConstanct;
import utils.FileIO;

public class CountAverageLengthOfToken {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=PathConstanct.PATH_SN_GROUP;
		String fpTrainSource=fopInput+"train.s";
		String fpTrainTarget=fopInput+"train.t";
		String fpTuneSource=fopInput+"tune.s";
		String fpTuneTarget=fopInput+"tune.t";
		String fpTestSource=fopInput+"test.s";
		String fpTestTarget=fopInput+"test.t";
		
		String[] arrTrainSource=FileIO.readFromLargeFile(fpTrainSource).split("\n");
		String[] arrTrainTarget=FileIO.readFromLargeFile(fpTrainTarget).split("\n");
		String[] arrTuneSource=FileIO.readFromLargeFile(fpTuneSource).split("\n");
		String[] arrTuneTarget=FileIO.readFromLargeFile(fpTuneTarget).split("\n");
		String[] arrTestSource=FileIO.readFromLargeFile(fpTestSource).split("\n");
		String[] arrTestTarget=FileIO.readFromLargeFile(fpTestTarget).split("\n");
		
		long lengthToken=0,numToken=0;
		
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
					lengthToken+=arrItems[j].length();
					numToken+=1;
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
					lengthToken+=arrItems[j].length();
					numToken+=1;
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
					lengthToken+=arrItems[j].length();
					numToken+=1;
				}
			}
		}
		
		double average=(lengthToken*1.0)/numToken;
		System.out.println(numToken+"\t"+average);
		
		
	}

}
