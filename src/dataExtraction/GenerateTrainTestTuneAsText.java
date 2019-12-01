 package dataExtraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import constanct.PathConstanct;
import utils.FileIO;

public class GenerateTrainTestTuneAsText {
	
	public static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		return (int)(Math.random() * ((max - min) + 1)) + min;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopSequence=PathConstanct.PATH_OUTPUT_ABBREV_CODE;
		String fopOutput=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME;
		
		String[] arrLocations=FileIO.readStringFromFile(fopSequence+"locations.txt").split("\n");
		String[] arrSource=FileIO.readStringFromFile(fopSequence+"source.txt").split("\n");
		String[] arrTarget=FileIO.readStringFromFile(fopSequence+"target.txt").split("\n");
		
		int numTuneAndTest=arrSource.length/100;
		
		HashSet<Integer> setTune=new LinkedHashSet<Integer>();
		HashSet<Integer> setTest=new LinkedHashSet<Integer>();
		
		while(setTune.size()<numTuneAndTest) {
			int numEle=getRandomNumberInRange(0,arrLocations.length-1);
			if(!setTune.contains(numEle)) {
				setTune.add(numEle);
			}
		}
		
		while(setTest.size()<numTuneAndTest) {
			int numEle=getRandomNumberInRange(0,arrLocations.length-1);
			if((!setTune.contains(numEle)) && (!setTest.contains(numEle))) {
				setTest.add(numEle);
			}
		}
		
		PrintStream sbTrainSource,sbTrainTarget,sbTuneSource,sbTuneTarget,sbTestSource,sbTestTarget,sbLineTrain,sbLineTune,sbLineTest,sbLocationTrain,sbLocationTune,sbLocationTest;
		
		try {
			sbTrainSource=new PrintStream(new FileOutputStream(fopOutput+"train.s",true));
			sbTrainTarget=new PrintStream(new FileOutputStream(fopOutput+"train.t",true));
			sbTuneSource=new PrintStream(new FileOutputStream(fopOutput+"tune.s",true));
			sbTuneTarget=new PrintStream(new FileOutputStream(fopOutput+"tune.t",true));
			sbTestSource=new PrintStream(new FileOutputStream(fopOutput+"test.s",true));
			sbTestTarget=new PrintStream(new FileOutputStream(fopOutput+"test.t",true));
			sbLineTrain=new PrintStream(new FileOutputStream(fopOutput+"line.train.txt",true));
			sbLineTune=new PrintStream(new FileOutputStream(fopOutput+"line.tune.txt",true));
			sbLineTest=new PrintStream(new FileOutputStream(fopOutput+"line.test.txt",true));
			sbLocationTrain=new PrintStream(new FileOutputStream(fopOutput+"location.train.txt",true));
			sbLocationTune=new PrintStream(new FileOutputStream(fopOutput+"location.tune.txt",true));
			sbLocationTest=new PrintStream(new FileOutputStream(fopOutput+"location.test.txt",true));
			
			for(int i=0;i<arrLocations.length;i++) {
				if(setTune.contains(i)) {
					sbLocationTune.println(arrLocations[i]);
					sbLineTune.println(i);
					sbTuneSource.println(arrSource[i]);
					sbTuneTarget.println(arrTarget[i]);
					
				} else if(setTest.contains(i)) {
					sbLocationTest.println(arrLocations[i]);
					sbLineTest.println(i);
					sbTestSource.println(arrSource[i]);
					sbTestTarget.println(arrTarget[i]);
				} else {
					sbLocationTrain.println(arrLocations[i]);
					sbLineTrain.println(i);
					sbTrainSource.println(arrSource[i]);
					sbTrainTarget.println(arrTarget[i]);
				}
			}
			
			sbTrainSource.close();
			sbTrainTarget.close();
			sbTuneSource.close();
			sbTuneTarget.close();
			sbTestSource.close();
			sbTestTarget.close();
			sbLineTrain.close();
			sbLineTune.close();
			sbLineTest.close();
			sbLocationTrain.close();
			sbLocationTune.close();
			sbLocationTest.close();
			
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
	}

}
