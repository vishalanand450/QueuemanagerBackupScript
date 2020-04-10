package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QmgrBackup {
	public static void main(String[] args) throws IOException, InterruptedException
	{
		ArrayList<String> qm_names = QmgrDisplay(); //calling QmgrDisplay method
		
		for(int x=0 ; x < qm_names.size() ; x++) {
			String s = qm_names.get(x);
			System.out.println(s);
		}
		
		ExecuteDmpCommand(qm_names);  //calling ExecuteDmpCommand method
	}

	public static ArrayList<String> QmgrDisplay() throws IOException, InterruptedException   //function for dspmq
	{
	    String cmd = "dspmq";
	    System.out.println("\n\nExecuting command: " + cmd);
	    Process p1 = Runtime.getRuntime().exec(cmd);
	    
	    int result1 = p1.waitFor();	    
	    System.out.println("Process exit code: " + result1);
	    System.out.println("\n");
	    System.out.println("Queue Managers present on the server :-");
	    
	    BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
	    ArrayList<String> arr1 = new ArrayList<String>();
	    String line1 ;
	    while((line1 = reader1.readLine()) != null)
	    {
	    	String command = RegEx(line1); //calling RegEx method
	    	arr1.add(command);
	    	
	    }
	  return arr1;
		
	}
	
	public static String RegEx(String str)  //function for filtering out Qmgr names
	{
		Pattern QM_PATTERN = Pattern.compile("\\([A-Z0-9]+\\)");
		Matcher qm_matcher = QM_PATTERN.matcher(str);
		String qm_value = "";
		while(qm_matcher.find()) {
    		String s = qm_matcher.group();
    		s = s.replace("(","");
    		qm_value = s.replace(")","");
		}
		return qm_value;
	}
	
	public static void ExecuteDmpCommand(ArrayList<String> qms) throws IOException, InterruptedException  //function for dmpmqcfg

	{
		for (int i = 0; i < qms.size(); i++) {
			String qm = qms.get(i);
			String cmd1 = "dmpmqcfg -m "+ qm;
    		System.out.println("\n\nExecuting command: " + cmd1);
    		Process p1 = Runtime.getRuntime().exec(cmd1);
		    BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));		                        
		    String line1;
		    File f = new File(qm+".mqsc");
		    f.createNewFile();
	    	FileWriter fr = new FileWriter(f);		    
		    while ((line1 = reader1.readLine()) != null) {
		    	fr.write(line1);
		    	fr.write(System.lineSeparator());
		    }  
		    fr.close();
    	    int result2 = p1.waitFor();	
    	    if(result2 ==0) 
    	    {
        	    System.out.println("Process exit code: " + result2);		    
    		    System.out.println("\nThe output has been written into the file: " + qm+".mqsc");    	    	
    	    }
    	    else 
    	    	System.out.println(qm + " :-Queue Manager is down");
		}
	}
	

}
