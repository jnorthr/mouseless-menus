package org.jnorthr.menus.support;

// sample json slurper from
// http://mrhaki.blogspot.fr/2011/11/grassroots-groovy-reading-json-with.html
import groovy.json.JsonSlurper;
import javax.swing.*;  
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import groovy.json.*  // for prettyPrint json

// code to read a json formatted text file that came from a firefox backup process ( NOT the export tool !!! )
public class ConsumeFirefoxBookmarks
{
    boolean audit = true;
    def path = "~"
    def outputpath = ".";   //"/Volumes/Media1/Software/menus/resources"
    def pwd=null;
    DateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss");
    List<String> entries = new ArrayList<String>();

    //get current date time with Date()
    Date date = new Date();
    def now = dateFormat.format(date);

    // void constructor
    public ConsumeFirefoxBookmarks()
    {
        boolean stopTask = false;
    
        def fc = new JFileChooser(path);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = fc.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) 
        {
            path = fc.getSelectedFile().getCanonicalPath()
            pwd = fc.getCurrentDirectory() 
            say "Chosen ${path}\npwd=${pwd}"
        }
        else
        {
            stopTask = true;
        } // end of else


        if (!stopTask)
        {
            def op = new JFileChooser(pwd);
            op.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    	outputpath = new File(outputpath).getCanonicalPath();
            op.setSelectedFile(new File("${outputpath}/firefoxbookmarks.txt"));
            int option2 = op.showSaveDialog(null);
            if (option2 == JFileChooser.APPROVE_OPTION) 
            {
                outputpath = op.getSelectedFile().getCanonicalPath()
                say "outputpath will be ${outputpath} \n"
				if (outputpath.indexOf(" ")>-1)
				{
					JOptionPane.showMessageDialog(null, "Your chosen output filename was improperly formatted.");
                	stopTask = true;				
				}
            }
            else
            {
                stopTask = true;
            } // end of else
    
        } // end of if

        if (stopTask) 
        {
            println "--> cancelled by user request."
            return;
        } // end of if

        else
        {
			if (path.trim().toLowerCase().endsWith(".json"))
			{
            	process(path, outputpath);			
			}
			else
			{			
				if (queryChoice(path)==0)
				{
            		process(path, outputpath);
				} // end of if
			} // end of else

        } // end of if

    } // end of constructor



    // 2 arg constructor: 1) input json file name and 2) output .txt filename for menus and 3) name of browser
    public ConsumeFirefoxBookmarks(String path, String outputpath, String browser)
    {
            process(path, outputpath, browser);
    } // end of constructor



    // print audit trail if flag is set
    public void say(tx)
    {
        if (audit)
        {
            println tx;
        } // end of if
    } // end of say



    // query if chosen input file does not end with .json suffix
    public int queryChoice(String nm)
    {
		def title = "Is Input File a JSON choice ?"
		def message = "Typically firefox backup filenames end with a .json suffix. Your choice of \n$nm does not.\nConfirm this is the correct file."
		int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) 
		{
			JOptionPane.showMessageDialog(null, "Ok, let's try !");
			return 0;
        }
        else 
		{
           JOptionPane.showMessageDialog(null, "Good-Bye !");
           return -1;
        } // end of else
    } // end of say


    // convert firefox bookmark epoc date to a nice string
    def convertDate(def epoch)
    {
        long dv = epoch / 1000;
        String dt = new java.text.SimpleDateFormat("EEE. dd MMM.yyyy HH:mm:ss").format(new java.util.Date (dv)).replace('/',' ');
        //long epoch = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse("01/01/1970 01:00:00").getTime() / 1000;
        return dt;
    } // end of def


    // confirm parms adhere to correct content and format
    def checkParms(String path, String outputname)
    {
        if (path==null || outputname==null || (path.trim().size() < 1) ||  (outputname.trim().size() < 1) )
        {
			def msg = "Fatal -> Either input filename of <${path}> or output filename of <${outputname}> can not be used.\n         Pls choose appropriate file names."
            println msg;
			JOptionPane.showMessageDialog(null, msg, "Fatal Condition", JOptionPane.ERROR_MESSAGE); 
            return true;            
        } // end of if    
    
        return false;
    } // end of check


    //find a date like 2013-08-23 in input filename so we can use that in the output file name too
    def findDateInFilename(String path, String ofn)
    {
        int i = path.trim().lastIndexOf('.');
        if ( i < 0 ) return ofn;
        int j = path.trim().lastIndexOf('/');
        int k = path.trim().lastIndexOf('\\');

        String mainname = path.substring(0,i);

        if ( j > -1 ) 
        {
            mainname = path.substring(j+1,i);
        } // end of if
        
        if ( k > -1 ) 
        {
            mainname = path.substring(k+1,i);
        } // end of if
        
        def np = mainname.split("-");
        say "\n... from <$path> we have a main name of <${mainname}> and np size="+np.size()

        if (np.size() < 2 ) return ofn;
		int yr = 0;
		try
		{
			yr = np[1] as Integer
		}
		catch(Exception w) 
		{
			return ofn;
		} // end of catch
		
        if (yr in 2000..2099)
        { 
            say "---> so year is :"+yr
            i = ofn.lastIndexOf('.');

            mainname = ofn.substring(0,i)
            np.eachWithIndex{p,ix-> if (ix>0) {mainname += '-'+p} }
            mainname += ofn.substring(i)
            say "--->  ended with mainname="+mainname
        }
        else
        {
            mainname = ofn;
        }
        return mainname;
    } // end of findDateInFilename


    // parse and write any bookmarks with a url - assume Firefox was origin browser  
    public void process(String path, String outputname)
    {
		process(path,outputname,"Firefox");
    } // end of 2 arg process


    // parse and write any bookmarks with a url   
    public void process(String path, String outputname, String browser)
    {
        if (checkParms(path, outputname))
            return;
            
        say "... reading ${path} and writing ${outputname}"
        String file2 
        boolean flag2 = (outputname.trim().startsWith("/")) ? true : false;
        if (flag2)
        { 
            file2 = outputname
        }
        else
        {
            file2 = new File(outputname).getCanonicalFile().toString();
        } // end of else
        
        say "... actual output file name :${file2}"
        if (new File(file2).isDirectory())
        {
			def msg = "Fatal -> ${file2} is a directory! Pls choose an output file name."
            println msg;
			JOptionPane.showMessageDialog(null, msg, "Fatal Condition", JOptionPane.ERROR_MESSAGE); 
            return;            
        } // end of if

        if (!(file2.toLowerCase().endsWith(".txt")))
        {
            file2 += ".txt"
            say "... adding .txt ="+file2
        } // end of if        
        
        
        file2 = findDateInFilename(path, file2);
        say "... so now we have file2="+file2
    
        def file1;
        try
        {
            file1 = new File(file2);
            file1.write  "// Bookmark Input File      : ${path}\n"
            file1.append "// This menu was created on : ${now}\n"
        }
        catch (IOException x)
        {
			def msg = "Fatal -> IOException working with '${outputname}' (${file2}) : \n" + x.getMessage()
            println msg;
			JOptionPane.showMessageDialog(null, msg, "Fatal Condition", JOptionPane.ERROR_MESSAGE); 
            return;
        } // end of catch
        

        def json;
        try
        {
            String json2 = "file:///${path}".toURL().getText("UTF-8")
			println JsonOutput.prettyPrint(json2);

            //String json2 = "file:////Volumes/Media/Users/jim/Desktop/ff_bookmarks-2013-04-30.json".toURL().text  
            // bookmarks-2013-04-29.json
            json = new JsonSlurper().parseText(json2)
        }
        catch (Exception x)
        {
			def msg = "Fatal -> Exception parsing json '${path}': \nIs this really a json formatted file ? \n" + x.getMessage()
            println msg;
			JOptionPane.showMessageDialog(null, msg, "Fatal Condition", JOptionPane.ERROR_MESSAGE); 
            return;
        } // end of catch


        def header = json.title
        say "json.title :"+header
        say "json.lastModified :"+json.lastModified

        say "bookmarks were extracted  :"+convertDate(json.lastModified)
        now = convertDate(json.lastModified)
        file1.append "// Bookmarks Last Updated   : ${now}\n\n"    
        file1.append "${browser} Bookmarks As Of ${now}:=*MENUTITLE\n\n";


        json.children.each{child-> 
            say "child:"+child+" -> "
            say "child.dateAdded:"+child.dateAdded
            say "child.title:"+child.title
            say "child.type:"+child.type

            def flag = (child.index) ? true : false;
            if (flag)
            {
                say "child.index:"+child.index
            } // end of if

            flag = (child.lastModified) ? true : false;
            if (flag)
            {
                say "child.lastModified:"+child.lastModified
            } // end of if

            flag = (child.root) ? true : false;
            if (flag)
            {
                say "child.root:"+child.root
            } // end of if

            if (child.annos)
            {
                say "child.annos:"+child.annos
                if (child.annos.value)
                {
                    say "child.annos.value :"+child.annos.value
                }
            } // end of if

            flag = (child.children) ? true : false;
            if (flag)
            {
                def titles=[]
                say "\nchild.children:"+child.children
                if (child.children.title)
                {
                    say "child.children.title.size() :"+child.children.title.size()        
                    say "child.children.title        :"+child.children.title        
                    child.children.title.eachWithIndex{e, ix ->
                         say "child.children.title     ${ix+1}  :"+e
                         titles << e;
                    } // end of each            
                } // end of if


                if (child.children.uri)
                {
                    say "child.children.uri.size() :"+child.children.uri.size()        
                    say "child.children.uri        :"+child.children.uri        
                    child.children.uri.eachWithIndex{e, ix ->
                        def show = false
                        if (e)
                        {
                            show = (e.toLowerCase().startsWith("place")) ? false : true ;
                            if (show)
                            {
                                say "     --->"+titles[ix]
				String entry = "${titles[ix]} :=${e}\n";
				entries << entry;
                            } // end of if
                        } // end of if
                 
                        say "child.children.uri     ${ix+1} show=${show} :"+e                 
                    } // end of each
            
                } // end of if

                if (child.children.type)
                {
                    say "child.children.type:"+child.children.type        
                } // end of if

            } // end of if

        } // end of each

	entries.sort();
	entries.each{e ->
	        file1.append e;   
	} // end of each

	file1.append "// Bookmark Count           : ${entries.size()}\n"

        say "---- the end ---"        
    } // end of process       


    // -----------------------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "--------------------------------------------------"
        print "... started "
        def fou = "./resources/firefoxbookmarks.txt";
        def fin = ""    
        def browser = "Unknown Browser"

        if (args.length>0) 
        {
            fin = args[0];
            if (args.length == 2 ) { fou = args[1]; }
            if (args.length == 3 ) { browser = args[2]; }
                
            println "... reading "+fin+"\n... writing "+fou;

            def fi = new File(fin);
            if (fi.exists())
            {
                println "\n... next, try constructor with 2 parms "
                org.jnorthr.menus.support.ConsumeFirefoxBookmarks mf = new org.jnorthr.menus.support.ConsumeFirefoxBookmarks(fin, fou, browser);
            } // end of if
        } // end of if

        else
        {
            org.jnorthr.menus.support.ConsumeFirefoxBookmarks mf = new org.jnorthr.menus.support.ConsumeFirefoxBookmarks();                
        } // end of if

        println "... the end "
        println "--------------------------------------------------\n"
    }    // end of main
    
} // end of class