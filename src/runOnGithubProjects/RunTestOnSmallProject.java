package runOnGithubProjects;

import utils.StanfordLemmatizer;
import visitor.AbbrevSequenceGenerator;

public class RunTestOnSmallProject {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String inPath="/Users/hungphan/Documents/workspace/SampleMethodInvocationProject/";
//		String outPath="/Users/hungphan/git/NLPLTranslation/sequences/SampleMethodInvocationProject/";
//		String inPath="/Users/hungphan/git/pig/";
//		String outPath="/Users/hungphan/git/NLPLTranslation/sequences/pig/";		
		String inPath="/Users/hungphan/Documents/workspace/TestExpInference/";
		String outPath="/Users/hungphan/git/dataDoesNMTOutperformSMT/textSequence/";
//		StanfordLemmatizer lemm=new StanfordLemmatizer();
		AbbrevSequenceGenerator mcsg=new AbbrevSequenceGenerator(inPath);
		mcsg.generateSequences(outPath);
		mcsg.generateAlignment(true);
	}

}
