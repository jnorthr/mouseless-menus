package org.jnorthr.menus;
import org.jnorthr.menus.MenuFile;
import org.jnorthr.menus.support.Validator;

public class MenuWrapper
{
    // show/hide audit trail msgs
    boolean audit = false;

    // handle to validator for a single line
    Validator val;
        
    // List of lines that would make good menu entries, excludes remarks lines // and *menutitle lines 
    List<Validator> wrapperLines = []  
    
    // base-zero pointer to starting anchor in this list
    int current = 0;     
    int displayed = 0;
    boolean fwd = true; // !fwd = walk backwards
    
    // no args constructor
    public MenuWrapper()
    {
        MenuFile mf = new MenuFile("/Volumes/DURACELL/mouseless-menus/resources/main.txt");
        println mf
        println "isMenuFile() ? :"+mf.isMenuFile();
        println "getTitle() :"+mf.getTitle();
        println "crtMenuEntry() :"+mf.crtMenuEntry()
        println "getFullFileName() :"+mf.getFullFileName();
        println "MenuFile getMenuLineCount() :"+mf.getMenuLineCount();
        
        // only copy over the valid menu lines not the remarks, etc
        mf.menuLines.each
        { e ->
            if (e.valid) 
            {
                this.wrapperLines << e;
            } // end of if
        };  // end of each
        
    } // end of method
    

    // how many menu lines
    public getLineCount()
    {
        return wrapperLines.size();
    } // end of getMenuLineCount()
    
    
    // complete dump of lines
    public showWrapperLines(List<Validator> wrapperLines)
    {
        wrapperLines.each
        { m -> 
            println m;
        } // end of each
            
        println "\nWe have ${ getLineCount() } lines"
    } // end of method
    
    
    // see if more lines can be taken after what's already been shown
    public hasNext()
    {
        println "---> hasNext() wrapperLines.size():"+wrapperLines.size()+" > current-1:${current-1} / "+current; 
        return ( wrapperLines.size() > current-1 ) ? true : false;        
    } // end of method
    
    // uses current as a pointer to front of lines list and howmany tells us the number of lines needed    
    public getNext(int howmany)
    {
        List<Validator> currentLines = []  
        //current += displayed;
        displayed = 0;
        
        // loop flag
        boolean flag = true;
        fwd = true; // what direction are we paging ?
        
        while(flag)
        {
            println "... current=$current displayed=$displayed; "
            displayed  += 1;
            currentLines << wrapperLines[current++]         
            if ( wrapperLines.size() <= current || displayed >= howmany  )
            {
                flag = false;
                println "... wrapperLines.size()=${wrapperLines.size()} < current=${current} displayed=$displayed; howmany-1:${howmany-1} "
            } // end of if
                
        } // end of while
        
        //ct.times{ println "-> $at:"+menuLines[at++].textComponent}
        
        return currentLines;    
    } // end of method
    
    
    // -----------------------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "--------------------------------------------------"
        println "... started"
        println " "
        MenuWrapper mw = new MenuWrapper();
        println "our Line Count() :"+mw.getLineCount()+" valid lines\n\nHere are the current wrapperLines :";
        mw.showWrapperLines(mw.wrapperLines);
        
        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";
        
        def next = mw.getNext(4);
        mw.showWrapperLines(next);
        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";
        
        next = mw.getNext(3);
        mw.showWrapperLines(next);

        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";
        
        next = mw.getNext(14);
        mw.showWrapperLines(next);

        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";

        next = mw.getNext(6);
        mw.showWrapperLines(next);

        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";

        next = mw.getNext(1);
        mw.showWrapperLines(next);
        println "\n\n "

/*        
        mw.current = 0;
        while(mw.hasNext())
        {
            next = mw.getNext(10);
            mw.showWrapperLines(next);        
        } // end of while
*/
        
        println "... the end "
        println "--------------------------------------------------"
    } // end of main
 }     // end of class    