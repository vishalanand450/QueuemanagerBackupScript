package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QmgrBackup {

	public static void main(String[] args) throws IOException, InterruptedException
	{
		ArrayList<String> x = QmgrDisplay();
		for (int i = 0; i < x.size(); i++) {
			System.out.println(x.get(i));
		}
	}
	
	public static ArrayList<String> QmgrDisplay() throws IOException, InterruptedException 
	{
	    String cmd = "dspmq";
	    System.out.println("\n\nExecuting command: " + cmd);
	    Process p1 = Runtime.getRuntime().exec(cmd);
	    
	    int result = p1.waitFor();	    
	    System.out.println("\nProcess exit code: " + result);
	    System.out.println("\n");
	    
	    BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
	    ArrayList<String> arr = new ArrayList<String>();
	    String line1 ;
	    while((line1 = reader1.readLine()) != null)
	    {
	    	arr.add(line1);
	    }
	    
	 return arr;   
		
	}
	
	

}
