package tool;

import java.io.File;
import java.util.ArrayList;
 
 
public class FileTool {
	public static ArrayList<String> refreshFileList(String strPath) 
	{ 
		ArrayList<String> filelist = new ArrayList<String>(); 
        File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        if (files == null) 
        {
            return filelist; 
        }
        for (int i = 0; i < files.length; i++) 
        {
            if (!files[i].getName().endsWith(".jpg")){
                continue;
            }
            if (files[i].isDirectory()) 
            { 
                refreshFileList(files[i].getAbsolutePath()); 
            } 
            else
            { 
                String strFileName = files[i].getAbsolutePath().toLowerCase(); 
                //System.out.println(strFileName); 
                filelist.add(files[i].getAbsolutePath());                    
            } 
        }
        return filelist;
	}
}