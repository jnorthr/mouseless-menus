package org.jnorthr.menus;
import org.jnorthr.menus.MenuFile;
import org.jnorthr.menus.support.Validator;

public class MenuWrapper
{
    // show/hide audit trail msgs
    boolean audit = true;

    // handle to validator for a single line
    Validator val;
        
    // List of lines that would make good menu entries, excludes remarks lines // and *menutitle lines 
    List<Validator> wrapperLines = []  
    
    // base-zero pointer to starting anchor in this list
    int current = 0;     
    int displayed = 0;
    boolean fwd = true; // !fwd = walk backwards

	// ===============================================================
	// class output debug / print internals
	// print text (maybe)
	public void say(def text) 
	{
		if (audit) println "$text" 
	} // end of say


    // no args constructor
    public MenuWrapper()
    {
		def mn = "/Volumes/DURACELL/mouseless-menus/resources/main.txt"
		new MenuWrapper(mn);        
    } // end of method
    
    
    // one args constructor
    public MenuWrapper(menuname)
    {
        MenuFile mf = new MenuFile(menuname);
        say mf
        say "isMenuFile() ? :"+mf.isMenuFile();
        say "getTitle() :"+mf.getTitle();
        say "crtMenuEntry() :"+mf.crtMenuEntry()
        say "getFullFileName() :"+mf.getFullFileName();
        say "MenuFile getMenuLineCount() :"+mf.getMenuLineCount();
        
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
            
        say "\nWe have ${ getLineCount() } lines"
    } // end of method
    

    // ==============================================================
    // see if more lines can be taken after what's already been shown
    public hasNext()
    {
        say "---> hasNext()  wrapperLines :"+wrapperLines.size()+" > current :${current}"; 
        return ( wrapperLines.size() > current ) ? true : false;        
    } // end of method
    

    // uses current as a pointer to front of lines list and howmany tells us the number of lines needed    
    public getNext(int howmany)
    {
		say "getNext($howmany) ..."
		
        List<Validator> currentLines = []  
		if ( !hasNext() ) return currentLines

        displayed = 0;
        
        // loop flag
        boolean flag = true;
        fwd = true; // what direction are we paging ?
        
        while(flag)
        {
            displayed  += 1;
            say "... current=$current displayed=$displayed; "
            currentLines << wrapperLines[current++]         
            if ( wrapperLines.size() <= current || displayed >= howmany  )
            {
                flag = false;
                say "... wrapperLines.size()=${wrapperLines.size()} < current=${current} displayed=$displayed; howmany-1:${howmany-1} "
            } // end of if
                
        } // end of while
        
        //ct.times{ println "-> $at:"+menuLines[at++].textComponent}
        
        return currentLines;    
    } // end of method
   

    // ==============================================================
    // see if lines can be taken before what's already been shown
    public hasPrior()
    {
        say "---> hasPrior() current :${current} < 1 ?"; 
        return ( current < 1 ) ? false : true;        
    } // end of method
   

 
    
    // -----------------------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "--------------------------------------------------"
        println "... started"
        println " "
        MenuWrapper mw = new MenuWrapper("./resources/main.txt");
        println "our Line Count() :"+mw.getLineCount()+" valid lines\n\nHere are the current wrapperLines :";
		def prior = mw.hasPrior();
		println "\nhasPrior()="+prior

        mw.showWrapperLines(mw.wrapperLines);
        
        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";
        
        def next = mw.getNext(4);
        mw.showWrapperLines(next);
        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";
		prior = mw.hasPrior();
		println "\nhasPrior()="+prior
        
        next = mw.getNext(3);
        mw.showWrapperLines(next);

        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";
        
        next = mw.getNext(14);
        mw.showWrapperLines(next);

        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";
		
		println "asking for more lines than are available"
        next = mw.getNext(7);
        mw.showWrapperLines(next);

        println "\nhasNext()="+mw.hasNext()+" : current=${mw.current} + displayed=${mw.displayed} and "+mw.getLineCount()+" lines";

		println "asking for 1 more line even after all rows were ready"
        next = mw.getNext(1);
        mw.showWrapperLines(next);
        println "\n\n===============================\n Ok, let's read rows 10 at a time ! "

        mw.current = 0;
        while(mw.hasNext())
        {
            next = mw.getNext(10);
            mw.showWrapperLines(next);        
        } // end of while

		prior = mw.hasPrior();
		println "\nhasPrior()="+prior
        
        println "... the end "
        println "--------------------------------------------------"
    } // end of main
 }     // end of class    