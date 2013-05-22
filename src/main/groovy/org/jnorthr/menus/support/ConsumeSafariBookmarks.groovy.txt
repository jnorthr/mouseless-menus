package org.jnorthr.menus.support;

// sample xml (html) slurper from
// read: http://www.frommknecht.net/2010/02/robust-html-parsing-the-groovy-way/
// and
// http://mrhaki.blogspot.fr/2011/11/grassroots-groovy-reading-json-with.html
// and groovy closure eamples:
// http://groovy.codehaus.org/Martin+Fowler%27s+closure+examples+in+Groovy

import javax.swing.*;  
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

// code to read a html formatted text file that came from a safari backup process ( NOT the export tool !!! )
public class ConsumeSafariBookmarks
{
    boolean audit = false;
    def path = "~"
    def outputpath = "/Volumes/Media1/Software/menus/resources"
    def pwd=null;
    DateFormat dateFormat = new SimpleDateFormat("EEE. dd MMM yyyy HH:mm:ss");
    
    //get current date time with Date()
    Date date = new Date();
    def now = dateFormat.format(date);

    // void constructor
    public ConsumeSafariBookmarks()
    {
        boolean stopTask = false;
    
        def fc = new JFileChooser(path);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = fc.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) 
        {
            path = fc.getSelectedFile().getAbsolutePath()
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
            op.setSelectedFile(new File("${outputpath}/safaribookmarks.txt"));
            int option2 = op.showSaveDialog(null);
            if (option2 == JFileChooser.APPROVE_OPTION) 
            {
                outputpath = op.getSelectedFile().getAbsolutePath()
                say "outputpath will be ${outputpath} \n"
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
            process(path, outputpath);
            //process(path, outputpath);
        } // end of if

    } // end of constructor


    // 2 arg constructor: 1) input html file name and 2) output .txt filename for menus
    public ConsumeSafariBookmarks(String path, String outputpath)
    {
            process(path, outputpath);
    } // end of constructor


    // print audit trail if flag is set
    public void say(tx)
    {
        if (audit)
        {
            println tx;
        } // end of if
    } // end of say


    // confirm parms adhere to correct content and format
    def checkParms(String path, String outputname)
    {
        if (path==null || outputname==null || (path.trim().size() < 1) ||  (outputname.trim().size() < 1) )
        {
            println "Fatal -> Either input filename of <${path}> or output filename of <${outputname}> can not be used.\n         Pls choose appropriate file names."
            return true;            
        } // end of if    
    
        return false;
    } // end of check


    // parse and write any bookmarks with a url   
    public void process(String path, String outputname)
    {
        if (checkParms(path, outputname))
            return;
            
        say "... reading ${path} and writing ${outputname}"
        def file2 
        boolean flag2 = (outputname.trim().startsWith("/")) ? true : false;
        if (flag2)
        { 
            file2 = outputname
        }
        else
        {
            file2 = new File(outputname).getAbsoluteFile();
        } // end of else
        
        say "... actual output file name :${file2}"
        if (new File(file2).isDirectory())
        {
            println "Fatal -> ${file2.toString()} is a directory! Pls choose an output file name."
            return;            
        } // end of if

        if (!(file2.toLowerCase().endsWith(".txt")))
        {
            file2 += ".txt"
            say "... adding .txt ="+file2
        } // end of if        
        
    
        def file1;
        try
        {
            file1 = new File(file2);
            file1.write "Bookmarks Menu:=*MENUTITLE\n";
            file1.append "// Bookmarks File :=${path}\n"
            file1.append "// This menu was created on : ${now}\n"
        }
        catch (IOException x)
        {
            println "Fatal -> IOException working with '${outputname}' (${file2}) : \n" + x.getMessage()
            //x.printStackTrace();
            return;
        } // end of catch
        
		def htmlParser; 
        def html;

        try
        {
			@Grab(group='org.ccil.cowan.tagsoup', module='tagsoup', version='1.2' )
			def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
			def slurper = new XmlSlurper(tagsoupParser)
			File file = new File(path)
			Reader streamReader = new InputStreamReader(new FileInputStream(file), "UTF-8")
			htmlParser = slurper.parse(streamReader);
        }
        catch (Exception x)
        {
            println "Fatal -> Exception parsing html '${path}': \nIs this really an html formatted file ? \n" + x.getMessage()
            return;
        } // end of catch


		htmlParser.'**'.findAll{ it.a }.each {e ->
    		def x = e.@href;
    		if (x.size()>0) 
			{
				say "... found:"+e+" := "+x 
				String xs = x.toString();
				if (!(xs.trim().toLowerCase().startsWith("place:")) )
				{
            		file1.append "${e} := ${x}\n"							
				} // end of if
			} // end of if
		} // end of each
 

       say "---- the end ---"        
    } // end of process       


    // -----------------------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "--------------------------------------------------"
        print "... started "
        def fou = "./resources/safaribookmarks.txt";
        def fin = "/Volumes/media/users/jim/Desktop/Safari Bookmarks.html"
        
        if (args.length>1) 
        {
            fin = args[0];
            fou = args[1];
                
            println "... reading "+fin+"\n... writing "+fou;

            def fi = new File(fin);
            if (fi.exists())
            {
                println "\n... next, try constructor with 2 parms "
                org.jnorthr.menus.support.ConsumeSafariBookmarks mf = new org.jnorthr.menus.support.ConsumeSafariBookmarks(fin, fou);
            } // end of if
        } // end of if

        else
        {
            org.jnorthr.menus.support.ConsumeSafariBookmarks mf = new org.jnorthr.menus.support.ConsumeSafariBookmarks();                
        } // end of if

        println "... the end "
        println "--------------------------------------------------\n"
    }    // end of main
    
} // end of class


/*  sample code
@Grab(group='org.ccil.cowan.tagsoup', module='tagsoup', version='1.2' )
// read: http://www.frommknecht.net/2010/02/robust-html-parsing-the-groovy-way/

def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
def slurper = new XmlSlurper(tagsoupParser)
File file = new File("/Volumes/media/users/jim/Desktop/Safari Bookmarks.html")
Reader streamReader = new InputStreamReader(new FileInputStream(file), "UTF-8")
def htmlParser = slurper.parse(streamReader)
 
// it.@a != 'question-hyperlink'
htmlParser.'**'.findAll{ it.@href.size() > 0 }.each {
    if (it.@href.size()>0) println "... found:"+it.@href
}
println "--- the end part one ---\n"

htmlParser.'**'.findAll{ it.a }.each {e ->
    def x = e.@href;
    if (x.size()>0) println "... found:"+e+" := "+x 
}

println "--- the end ---"

*/