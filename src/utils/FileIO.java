package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileIO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static String tryGetLine(BufferedReader br) {
		String line=null;
		try {
			line = br.readLine();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}
	public static void copyFileUsingChannel(File source, File dest) {
	    FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
	    try {
	        sourceChannel = new FileInputStream(source).getChannel();
	        destChannel = new FileOutputStream(dest).getChannel();
	        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	       }
	    catch(Exception ex){
	    	ex.printStackTrace();
	    }
	    finally{
	    	try{
	    		sourceChannel.close();
		        destChannel.close();
	    	}catch(Exception ex){
	    		
	    	}
	           
	   }
	}
	
	public static void copyFileReplaceExist(String source, String dest) {
	    try {
	    	Path pSource=Paths.get(source);
	    	Path pTarget=Paths.get(dest);
	    	Files.copy(pSource, pTarget, StandardCopyOption.REPLACE_EXISTING);
	       }
	    catch(Exception ex){
	    	ex.printStackTrace();
	    }
	    
	}
	
	public static void writeStringToFile(String string, String outputFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(string);
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		//	System.exit(0);
			System.err.println(e.getMessage());
		}
	}
	
	public static void appendStringToFile(String string, String outputFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile,true));
			writer.write(string);
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			/*e.printStackTrace();
			System.exit(0);*/
			System.err.println(e.getMessage());
		}
	}
	
	public static String readStringFromFile(String inputFile) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
			byte[] bytes = new byte[(int) new File(inputFile).length()];
			in.read(bytes);
			in.close();
			return new String(bytes);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String readFromLargeFile(String inputFile){
		StringBuilder sbResult=new StringBuilder();
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(inputFile), StandardCharsets.ISO_8859_1)) {
		    
			for (String line = null; (line = tryGetLine(br)) != null;) {
		    	sbResult.append(line+"\n");
//				prevLine=line;
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return sbResult.toString();
		
		
	}
	
	public static void findFiles(File root, String extension,
			ArrayList<String> lstFilePaths) {
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(extension)) {
				lstFilePaths.add(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				findFiles(file, extension, lstFilePaths);
			}
		}
	}

	public static String[] findAllJavaFiles(String inputPath) {
		ArrayList<String> lstRersult = new ArrayList<String>();
		File fileInput = new File(inputPath);
		findFiles(fileInput, "java", lstRersult);

		return convertToArrString(lstRersult);
	}
	
	public static String[] findAllJarFiles(String inputPath) {
		ArrayList<String> lstRersult = new ArrayList<String>();
		File fileInput = new File(inputPath);
		findFiles(fileInput, "jar", lstRersult);

		return convertToArrString(lstRersult);
	}
	
	public static String[] combineFilesToArray(String jdkPath,String[] arr) {
		String[] arrTotal=new String[1+arr.length];
		arrTotal[0]=jdkPath;
		for(int i=0;i<arr.length;i++){
			arrTotal[i+1]=arr[i];
		}

		return arrTotal;
	}
	
	public static String[] findAllExtensionFiles(String inputPath,String extension) {
		ArrayList<String> lstRersult = new ArrayList<String>();
		File fileInput = new File(inputPath);
		findFiles(fileInput, "java", lstRersult);

		return convertToArrString(lstRersult);
	}
	
	public static String[] convertToArrString(ArrayList<String> lstInput) {
		String[] arrResult = new String[lstInput.size()];
		for (int i = 0; i < lstInput.size(); i++) {
			arrResult[i] = lstInput.get(i);
		}
		return arrResult;
	}

}
