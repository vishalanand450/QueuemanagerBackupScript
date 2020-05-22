package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class QmgrBackup {
	public static void main(String[] args) throws IOException, InterruptedException, AddressException, MessagingException
	{

		String host = Hostname() ;  //calling HostName function
		System.out.println("\nHostname is " + host);
		
		ArrayList<String> qm_names = QmgrDisplay(); //calling QmgrDisplay function
		for(int x=0 ; x < qm_names.size() ; x++) 
		{
			String s = qm_names.get(x);
			System.out.println(s);
		}
		
		String DmpCommand = ExecuteDmpCommand(qm_names) ;  //calling ExecuteDmpCommand function
		System.out.println("\nResults-") ;
		System.out.println(DmpCommand);
		
		archiveFiles() ;
		
//		DeleteOldFile() ;
		
		
		mailFunction(DmpCommand) ;  //calling mail function
	}

	public static ArrayList<String> QmgrDisplay() throws IOException, InterruptedException   //function for dspmq
	{
	    
	    Process p1 = null;
	    if(CheckWindowOS()) 
	    {
	    	String[] wincmd1 = {"cmd.exe", "/c",  "dspmq"};
	    	p1 = Runtime.getRuntime().exec(wincmd1);
		    System.out.println("\n\nExecuting command: " + wincmd1[2]);
	    } 
	    else 
	    {
	    	String cmd = "dspmq";
	    	p1 = Runtime.getRuntime().exec(cmd);
		    System.out.println("\n\nExecuting command: " + cmd);
	    }
	    
	    
	    int result1 = p1.waitFor();	    
	    System.out.println("Process exit code: " + result1);
	    System.out.println("\n");
	    System.out.println("Queue Managers present on the server :-");
	    
	    BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
	    ArrayList<String> arr1 = new ArrayList<String>();
	    String line1 = "" ;
	    while((line1 = reader1.readLine()) != null)
	    {
	    	String queueManagerName = RegEx(line1); //calling RegEx method
	    	arr1.add(queueManagerName);
	    	
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
	
	public static String ExecuteDmpCommand(ArrayList<String> qms) throws IOException, InterruptedException  //function for dmpmqcfg

	{
		String output = "" ;
		for (int i = 0; i < qms.size(); i++) 
		{
			if (CheckWindowOS()) 
			{
				String qm = qms.get(i);
				String[] wincmd1 = {"cmd.exe", "/c", "dmpmqcfg -m "+ qm + " -a all -o 1line"};
	    		System.out.println("\n\nExecuting command: " + wincmd1[2]);
	    		Process p2 = Runtime.getRuntime().exec(wincmd1);
			    BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
			    BufferedReader stdError = new BufferedReader(new  InputStreamReader(p2.getErrorStream()));
			    String line2;
			    File queueManagerLogDir = new File("E:\\home\\mqm\\log\\queueManagerLog");
			    if (!queueManagerLogDir.exists()) {
			    	queueManagerLogDir.mkdirs() ;
			    }
			    File f = new File("E:\\home\\mqm\\log\\queueManagerLog\\"+qm+"_"+GetCurrentTimeStamp()+".mqsc" );
//			    System.out.println(f.getPath());
			    f.createNewFile();
			    FileWriter fr = new FileWriter(f);		    
			    while ((line2 = reader2.readLine()) != null) 
			    {
			    	fr.write(line2);
			    	fr.write(System.lineSeparator());
			    } 
			    while ((line2 = stdError.readLine()) != null) 
		        {
		            System.out.println(line2);
		        }
			    fr.close();
	    	    int result2 = p2.waitFor();
	    	    
	    	    if(result2 ==0) 
	    	    { 
	    	    	String a = qm + " is up. Backup has been written into the file: " + qm+"_"+GetCurrentTimeStamp()+".mqsc on path "+queueManagerLogDir ;    		    
	        	    System.out.println("Process exit code: " + result2);
//	        	    output = output +a+ "\n" ;
	        	    output += a+ "\n" ;
	    	    }
	    	    else 
	    	    {
	    	    	String b = qm + " is down. Backup failed." ;
	    	    	System.out.println("Process exit code: " + result2);
	    	    	f.delete();
	    	    	output += b+ "\n" ;
	    	    	
	    	    }
				
			}
			
			else 
			{
				String qm = qms.get(i);
				String cmd2 = "dmpmqcfg -m "+ qm + " -a all -o 1line";
	    		System.out.println("\n\nExecuting command: " + cmd2);
	    		Process p2 = Runtime.getRuntime().exec(cmd2);
			    BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));		                        
			    String line2;
			    File queueManagerLogDir = new File("/home/mqm/log/queueManagerLog" );
			    if (!queueManagerLogDir.exists()) {
			    	queueManagerLogDir.mkdirs() ;
			    }
//			    System.out.println(f.getPath());
			    File f = new File("/home/mqm/log/queueManagerLog/"+qm+"_"+GetCurrentTimeStamp()+".mqsc" );
			    f.createNewFile();
			    FileWriter fr = new FileWriter(f);		    
			    while ((line2 = reader2.readLine()) != null) 
			    {
			    	fr.write(line2);
			    	fr.write(System.lineSeparator());
			    }  
			    fr.close();
	    	    int result2 = p2.waitFor();
	    	    if(result2 ==0) 
	    	    { 
	    	    	String a = qm + " is up. Backup has been written into the file: " + qm+"_"+GetCurrentTimeStamp()+".mqsc on path "+queueManagerLogDir ;    		    
	        	    System.out.println("Process exit code: " + result2);
//	        	    output = output +a+ "\n" ;
	        	    output += a+ "\n" ;
	    	    }
	    	    else 
	    	    {
	    	    	String b = qm + " is down. Backup failed." ;
	    	    	System.out.println("Process exit code: " + result2);
	    	    	f.delete();
	    	    	output += b+ "\n" ;
	    	    	
	    	    }
				
			}
		
	    	
		}
		return output;
	}
	
	public static String Hostname() throws IOException 
	{
		String cmd3 = "hostname" ;
		Process p3 = Runtime.getRuntime().exec(cmd3);
		String hostname = "";
		BufferedReader reader3 = new BufferedReader(new InputStreamReader(p3.getInputStream()));
		
		String line3;
		
		while ((line3 = reader3.readLine()) != null) 
		{
	    	hostname = line3;
	    } 
		return hostname ;
	}
	
	public static void mailFunction (String text) throws AddressException, MessagingException, IOException 
	{
		
		final String mailServer = "smtp-z1-nomx.lilly.com" ;
		final String username = "Maintenance_USMAIL-GMPNT@lilly.com" ;
		final String password = "" ;
		
		String fromAddress = "Maintenance_USMAIL-GMPNT@lilly.com" ;
		String toAddress = "anand_vishal@network.lilly.com" ;
//		String ccAddress = "" ;
		String subject = "Queue Manager backup on " +Hostname() ;
		String message = "Hello Team,\n\nQueue manager backup script successfully ran on " +Hostname()+ "\n\nResults:-\n\n"+text+ "\n\nThanks" ;
		
		Properties properties = System.getProperties() ;
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host" , mailServer) ;
		properties.put("mail.smtp.auth" , true) ;
		properties.put("mail.smtp.port" , 25);
		

		Session session = Session.getInstance(properties) ;
		MimeMessage msg = new MimeMessage(session) ;
		
		msg.setFrom(new InternetAddress(fromAddress));
		msg.addRecipients(Message.RecipientType.TO, toAddress);
//		msg.addRecipients(Message.RecipientType.CC, ccAddress);
		msg.setSubject(subject);
		msg.setText(message);	

		
		
		Transport tr = session.getTransport("smtp");
		tr.connect(mailServer, username);
		tr.sendMessage(msg, msg.getAllRecipients());
		tr.close();
		
		System.out.println("\nEmail sent successfully to MQ team.\n\n");	
	}
	
	public static String GetCurrentTimeStamp() 
	{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); 
        Date now = new Date();
        String dateTimeStamp = dateFormat.format(now);
        return dateTimeStamp;
    }
	
	public static void DeleteOldFile() 
	{
		File f = new File ("/jackfruit/MQ_installer/QMGR_backup/") ;
		
		if (f.exists())
		{ 
            File[] listOfFiles = f.listFiles();
 
            long daysForDeletion = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000);
 
            for (File listFile: listOfFiles) 
            { 
                if (listFile.getName().contains(".mqsc") && listFile.lastModified() < daysForDeletion) 
                {                	            	
                	listFile.delete() ;
                	
                }               	
               
            }
            
		}
		
	}
	
	public static void archiveFiles() 
	{
		File archiveFolder = null;
		File sourceFolder = null;
		long deleteArchiveTime = System.currentTimeMillis() - (5 * 1000);
		
		if(CheckWindowOS()) 
		{
			archiveFolder = new File("E:\\home\\mqm\\log\\queueManagerLog\\archive");
		    sourceFolder = new File("E:\\home\\mqm\\log\\queueManagerLog");
			
		}
		
		else 
		{
			archiveFolder = new File("/home/mqm/log/queueManagerLog/archive/");
		    sourceFolder = new File("/home/mqm/log/queueManagerLog/");
			
		}

	    if (!archiveFolder.exists())
	    {
	    	archiveFolder.mkdirs();
	    }
	    
	    if (sourceFolder.exists() && sourceFolder.isDirectory())
	    {
	        File[] listOfFiles = sourceFolder.listFiles();  
	        // Get list of the files and iterate over them
	        
	        for (File child : listOfFiles )
	            {
	        	    if (child.lastModified() < deleteArchiveTime )
	        	    	
	        	    	if (CheckWindowOS())
	        	    	{
	        	    		child.renameTo(new File(archiveFolder + "\\" + child.getName()));	        	    		
	        	    	}
	        	    
	        	    	else
	        	    	{
	        	    		child.renameTo(new File(archiveFolder + "/" + child.getName()));	        	    		
	        	    	}
	                
	            }
	    }
	    
	}
	
	public static boolean CheckWindowOS()  // this method returns true if OS is windows
	{
		String OS = System.getProperty("os.name") ;
//		System.out.println("\nOperating system = "+OS);
		if (OS.contains("nux")) 
		{
			return false;
		}
		return true;
		
	}

}
