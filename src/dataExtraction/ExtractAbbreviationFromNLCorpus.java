package dataExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import constanct.PathConstanct;
import utils.FileIO;
import utils.FileUtil;

public class ExtractAbbreviationFromNLCorpus {

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
			String[] arrSource = lstInSource[i].trim().split("\\s+");
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

				sbResultSource.append(sbItemSource.toString() + "\n");
				sbResultTarget.append(sbItemTarget.toString() + "\n");
			
			
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
		String fopInput = PathConstanct.fop_NL_Data;
		String fopOutput = PathConstanct.fop_NL_Data + "abbrevResolve" + File.separator;
		String fpFullTextTrain = fopInput + "train.tok.clean.bpe.32000.en";
		String fpFullTextValid = fopInput + "newstest2013.tok.bpe.32000.en";
		String fpFullTextTest = fopInput + "newstest2015.tok.bpe.32000.en";
		String fpFullTextVocab = fopInput + "vocab.bpe.32000.en";

//		String[] arrTrainText=FileIO.readFromLargeFile(fpFullTextTrain).split("\n");
//		String[] arrValidText=FileIO.readFromLargeFile(fpFullTextValid).split("\n");
//		String[] arrTestText=FileIO.readFromLargeFile(fpFullTextTest).split("\n");

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

		System.out.println("finish vocab");

	}

}
