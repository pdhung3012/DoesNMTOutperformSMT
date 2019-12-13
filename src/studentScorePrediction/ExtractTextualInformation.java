package studentScorePrediction;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.File;
import java.util.HashSet;

import constanct.MLScoringPath;
import utils.FileIO;

public class ExtractTextualInformation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=MLScoringPath.PATH_Extract;
		String fopOutput=MLScoringPath.PATH_TEXTUAL_INFORMATION;
		
		File folderInput=new File(fopInput);
		File[] arrInput=folderInput.listFiles();
		int count=0;
		String strNames="";
		HashSet<String> setExtensions=new HashSet<String>();
		setExtensions.add("js");
		setExtensions.add("html");
		setExtensions.add("htm");
		for(int i=0;i<arrInput.length;i++) {
			if(arrInput[i].isDirectory()) {
				
				
				String[] arrFileJS=FileIO.findAllExtensionFiles(arrInput[i].getAbsolutePath()+File.separator,setExtensions);
				System.out.println(arrInput[i].getAbsolutePath()+"\t"+arrFileJS.length);
				StringBuilder sbResult=new StringBuilder();
				for(int j=0;j<arrFileJS.length;j++) {
					String strItem=FileIO.readStringFromFile(arrFileJS[j])+"\n";
					sbResult.append(strItem);
				}
				
				
				if(!sbResult.toString().isEmpty()) {
					String fopOutItem=fopOutput+arrInput[i].getName()+File.separator;
					new File(fopOutItem).mkdir();
					FileIO.writeStringToFile(sbResult.toString(), fopOutItem+"text.txt");
					count++;
					strNames+=arrInput[i].getName()+"\n";
					System.out.println();
				}
			}
			
		}
		FileIO.writeStringToFile(strNames, fopOutput+"listStudents.txt");
		
	}

}
