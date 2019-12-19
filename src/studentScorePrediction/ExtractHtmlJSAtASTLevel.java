package studentScorePrediction;

import java.io.File;
import java.util.HashSet;

import constanct.MLScoringPath;
import utils.FileIO;

public class ExtractHtmlJSAtASTLevel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=MLScoringPath.PATH_Extract;
		String fopOutput=MLScoringPath.PATH_TEXTUAL_INFORMATION;
		String fnListHtmlFile="listHtmlFiles.txt";
		String fnListJSFile="listJSFiles.txt";
		
		new File(fopOutput).mkdir();
		File folderInput=new File(fopInput);
		File[] arrInput=folderInput.listFiles();
		int count=0;
		String strNames="";
		HashSet<String> setExtensionsJS=new HashSet<String>();
		setExtensionsJS.add("js");
		
		HashSet<String> setExtensionsHtml=new HashSet<String>();
		setExtensionsHtml.add("html");
		setExtensionsHtml.add("htm");
		
		for(int i=0;i<arrInput.length;i++) {
			if(arrInput[i].isDirectory()) {
				
				
				String[] arrFileJS=FileIO.findAllExtensionFiles(arrInput[i].getAbsolutePath()+File.separator,setExtensionsJS);
				System.out.println(arrInput[i].getAbsolutePath()+"\t"+arrFileJS.length);
				StringBuilder sbResult=new StringBuilder();
				for(int j=0;j<arrFileJS.length;j++) {
					String strItem=arrFileJS[j]+"\n";
					sbResult.append(strItem);
				}
				
				String[] arrFileHtml=FileIO.findAllExtensionFiles(arrInput[i].getAbsolutePath()+File.separator,setExtensionsHtml);
				System.out.println(arrInput[i].getAbsolutePath()+"\t"+arrFileHtml.length);
				StringBuilder sbHtml=new StringBuilder();
				for(int j=0;j<arrFileHtml.length;j++) {
					String strItem=arrFileHtml[j]+"\n";
					sbHtml.append(strItem);
				}
				
				
				if((!sbResult.toString().isEmpty()) &&(!sbHtml.toString().isEmpty())  ) {
					String fopOutItem=fopOutput+arrInput[i].getName()+File.separator;
					new File(fopOutItem).mkdir();
					FileIO.writeStringToFile(sbResult.toString(), fopOutItem+fnListJSFile);
					FileIO.writeStringToFile(sbHtml.toString(), fopOutItem+fnListHtmlFile);					
					count++;
					strNames+=arrInput[i].getName()+"\n";
					System.out.println();
				}
			}
			
		}
		FileIO.writeStringToFile(strNames, fopOutput+"listStudents.txt");
		
	}

}
