package studentScorePrediction;

import java.io.File;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import constanct.MLScoringPath;
import utils.FileIO;

public class ExtractHtmlTextContent {
	
	public static String getHtmlNode(String filePath) {
		String strResult="";
		String strContent=FileIO.readStringFromFile(filePath);
		try {
			Document doc = Jsoup.parse(strContent);
			Elements allNodes = doc.getAllElements();
			for(int i=0;i<allNodes.size();i++) {
				if(allNodes.get(i).children().size()==0) {
					strResult+=allNodes.get(i).nodeName()+" ";
				}
				
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return strResult.trim();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=MLScoringPath.PATH_TEXTUAL_INFORMATION;
		String fopOutput=MLScoringPath.PATH_TEXTUAL_INFORMATION;
		String fnListHtmlFile="listHtmlFiles.txt";
		String fnTextHtmlFile="textHtml.txt";
		
		new File(fopOutput).mkdir();
		File folderInput=new File(fopInput);
		File[] arrInput=folderInput.listFiles();
		int count=0;
		
		
		HashSet<String> setExtensionsHtml=new HashSet<String>();
		setExtensionsHtml.add("html");
		setExtensionsHtml.add("htm");
		
		for(int i=0;i<arrInput.length;i++) {
			if(arrInput[i].isDirectory()) {
				String[] arrHtmls=FileIO.readStringFromFile(arrInput[i].getAbsolutePath()+"/"+fnListHtmlFile).split("\n");
				
				String textHtml="";
				for(int j=0;j<arrHtmls.length;j++) {
					textHtml+=getHtmlNode(arrHtmls[j])+" ";
				}
				FileIO.writeStringToFile(textHtml, arrInput[i].getAbsolutePath()+"/"+fnTextHtmlFile);
			}
			
		}

	}

}
