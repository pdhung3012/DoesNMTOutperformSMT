package studentScorePrediction;

import java.io.File;

import constanct.MLScoringPath;
import net.lingala.zip4j.exception.ZipException;
import utils.FileIO;
import utils.ZipUtil;

public class ExtractAndCollectData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopInput=MLScoringPath.PATH_HW_SUBMISSION;
		String fopOutput=MLScoringPath.PATH_Extract;
		
		File folderInput=new File(fopInput);
		File[] arrInput=folderInput.listFiles();
		
		int count=0;
		for(int i=0;i<arrInput.length;i++) {
			String[] arrContent=arrInput[i].getName().split("_");
			if(arrContent.length>=2 && arrContent[1].equals("LATE")) {
				continue;
			}
			count++;
			String studentName=arrInput[i].getName().split("_")[0];
			System.out.println(studentName);
			String fopStudentFolder=fopOutput+studentName+File.separator;
			new File(fopStudentFolder).mkdir();
			if(arrInput[i].isFile()) {
				if(arrInput[i].getName().endsWith("zip")) {
					try {
						ZipUtil.extractZipFileToFolder(arrInput[i].getAbsolutePath(), fopStudentFolder);
					} catch (ZipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(fopStudentFolder);
					}
				} else {
					FileIO.copyFileReplaceExist(arrInput[i].getAbsolutePath(), fopStudentFolder+arrInput[i].getName());
				}
			}
		}
		
		System.out.println(count);

	}

}
