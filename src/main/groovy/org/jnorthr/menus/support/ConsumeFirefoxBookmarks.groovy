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

// code to read a json formatted text file that came from a firefox backup process ( NOT the export tool !!! )
public class ConsumeFirefoxBookmarks
{
    boolean audit = false;
    def path = "~"
    def outputpath = "/Volumes/Media1/Software/menus/resources"
    def pwd=null;
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
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
            op.setSelectedFile(new File("${outputpath}/firefoxbookmarks.txt"));
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


    // 2 arg constructor: 1) input json file name and 2) output .txt filename for menus
    public ConsumeFirefoxBookmarks(String path, String outputpath)
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



    // convert firefox bookmark epoc date to a nice string
    def convertDate(def epoch)
    {
        long dv = epoch / 1000;
        String dt = new java.text.SimpleDateFormat("dd MMM.yyyy HH:mm:ss").format(new java.util.Date (dv)).replace('/',' ');
        //long epoch = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse("01/01/1970 01:00:00").getTime() / 1000;
        return dt;
    } // end of def


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
        int yr = np[1] as Integer
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
        
        
        file2 = findDateInFilename(path, file2);
        say "... so now we have file2="+file2
    
        def file1;
        try
        {
            file1 = new File(file2);
            file1.write "Firefox Bookmark Menus:=*MENUTITLE\n";
            file1.append "// Bookmarks File :=${path}\n"
            file1.append "// This menu was created on : ${now}\n"
        }
        catch (IOException x)
        {
            println "Fatal -> IOException working with '${outputname}' (${file2}) : \n" + x.getMessage()
            //x.printStackTrace();
            return;
        } // end of catch
        

        def json;
        try
        {
            String json2 = "file:///${path}".toURL().getText("UTF-8")
            //String json2 = "file:////Volumes/Media/Users/jim/Desktop/ff_bookmarks-2013-04-30.json".toURL().text  
            // bookmarks-2013-04-29.json
            json = new JsonSlurper().parseText(json2)
        }
        catch (Exception x)
        {
            println "Fatal -> Exception parsing json '${path}': \nIs this really a json formatted file ? \n" + x.getMessage()
            //x.printStackTrace();
            return;
        } // end of catch


        def header = json.title
        say "json.title :"+header
        say "json.lastModified :"+json.lastModified

        say "bookmarks were extracted  :"+convertDate(json.lastModified)
        now = convertDate(json.lastModified)
        file1.append "// Bookmarks Last Updated   : ${now}\n\n"    

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
                                //file1.append "${titles[ix]} :=${e}\n";
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
		println e;
	        file1.append e;   
	} // end of each


        say "---- the end ---"        
    } // end of process       


    // -----------------------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "--------------------------------------------------"
        print "... started "
        def fou = "./resources/firefoxbookmarks.txt";
        def fin = "/Volumes/Media1/Software/menus/src/main/resources/"    
        
        if (args.length>1) 
        {
            fin = args[0];
            fou = args[1];
                
            println "... reading "+fin+"\n... writing "+fou;

            def fi = new File(fin);
            if (fi.exists())
            {
                println "\n... next, try constructor with 2 parms "
                org.jnorthr.menus.support.ConsumeFirefoxBookmarks mf = new org.jnorthr.menus.support.ConsumeFirefoxBookmarks(fin, fou);
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