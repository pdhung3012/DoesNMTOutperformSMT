package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import consts.PathConstanct;

import java.io.FileInputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ZipUtil {

	public static boolean extractZipFileToFolder(String inputZipFile,String outputFolder) throws ZipException{
		// TODO Auto-generated method stub
				 //Open the file
				boolean isExtractSuccess=false;
				
	        	File fNewLocation=new File(outputFolder+File.separator);
				if(!fNewLocation.isDirectory()){
					fNewLocation.mkdir();
				}
				ZipFile zipFile = new ZipFile(inputZipFile);
		         zipFile.extractAll(outputFolder);
			     isExtractSuccess=true;
		         return isExtractSuccess;
	}
	
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
         
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
         
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
         
        return destFile;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 //Open the file
      
	}

}
