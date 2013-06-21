// thought this might be a nice array wrapper for menu lines
// allows logic to 'walk' thru an array of menu items, first,last,next,prior pages of items
package org.jnorthr.menus;
import org.jnorthr.menus.MenuFile;
import org.jnorthr.menus.support.Validator;

public class MenuWrapper
{
    // show/hide audit trail msgs
    boolean audit = true;

    // handle to validator for a single line
    Validator val;
        
    // List of lines that would make good menu entries, excludes remarks lines 
    // and *menutitle lines; holds all valid non-remark lines from this menufile 
    List<Validator> wrapperLines = []  
    
	// original menu text file was here:
	MenuFile mf;

    // base-zero pointer to starting anchor in this list
    int current = 0;  
    
    // how many lines in current page ?   
    int displayed = 0;
    
    // !fwd = walk backwards
    boolean fwd = true; 

	// holds one page of menu lines to display on a single menu pane; a subset of wrapperLines
    List<Validator> currentLines = []


	// -------------------------------
    // no args constructor
    public MenuWrapper()
    {
		this("./resources/main.txt");
    } // end of method
    
    
    // one args constructor
    public MenuWrapper(menuname)
    {
        mf = new MenuFile(menuname);
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
    


	// ===============================================================
	// class output debug / print internals
	// print text (maybe)
	public void say(def text) 
	{
		if (audit) println "$text" 
	} // end of say


	// accessor for dialog title
	public getTitle()
	{
		say "... MenuWrapper reports title as :"+mf.getTitle()
		return (mf.isMenuFile()) ? mf.getTitle() : ""	
	} // end of method
    
    
    // how many menu lines ?
    public getLineCount()
    {
        return wrapperLines.size();
    } // end of getMenuLineCount()
    
    
    // complete dump of lines for audit trail purposes
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
    

    // uses current as a pointer to front of lines list and howmany tells us the number of lines needed to be returned, if available
    // will return empty currentLines if no 'next' else returns up to a maximum of 'howmany' lines, though less if end of rows found
    public getNext(int howmany)
    {
		say "getNext($howmany) ..."
		
        currentLines = []  
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
   



	// pull off first 10 lines from the start of the list
    public getFirst()
    {
        return getFirst(10);    
    } // end of method


	// pull off howmany lines from the start of the list
    public getFirst(int howmany)
    {
		say "getFirst($howmany) ..."
		current = 0;
        currentLines = []  		

		int ct = getLineCount(); // 0..n lines
		if (ct < 1) return currentLines; // if no lines
		
		// if request is for more lines than available, adjust howmany to what's there
		if (howmany > ct ) howmany = ct;	

        return getNext(howmany);    
    } // end of method
    
    
	// -------------------------------------------
	// pull off 10 lines from the tail of the list
    public getLast()
    {
        return getLast(10);    
    } // end of method


	// pull off howmany lines from the tail of the list
    public getLast(int howmany)
    {
		say "getLast($howmany) ..."
        currentLines = []  
		current = 0;

		int ct = getLineCount(); // 0..n lines
		if (ct < 1) return currentLines; // if no lines
		
		// if request is for more lines than available, adjust howmany to what's there
		if (ct < howmany) howmany = ct;	

		current = ct;
        return getPrior(howmany);    
    } // end of method


    // ==============================================================
    // see if lines can be taken before what's already been shown
    public hasPrior()
    {
        say "---> hasPrior() current :${current} < 1 ?"; 
        return ( current < 1 ) ? false : true;        
    } // end of method
   
   
    // uses current as a pointer to front of lines list and howmany tells us the number of lines needed to be returned 
	// before 'current' line, if available
    // will return empty currentLines if no 'prior' available else returns up to a maximum of 'howmany' lines, 
	// though less if end of rows found
    public getPrior(int howmany)
    {
		say "getPrior($howmany) ..."
		
        currentLines = []  
		if ( !hasPrior() ) return currentLines

        displayed = 0;
        
        // loop flag
        boolean flag = true;
        fwd = false; // what direction are we paging ? backward...
        
        while(flag)
        {
            displayed  += 1;
			current -= 1;	
            say "... current=${current} displayed=$displayed; "
            currentLines << wrapperLines[current]         
            if ( current < 1 || displayed >= howmany  )
            {
                flag = false;
                say "... current=${current} < 0 || displayed :$displayed  >=  howmany :${howmany-1} "
            } // end of if
                
        } // end of while
                
        return currentLines;    
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
        
		println "asking for 1 more line even after all rows were ready"
		next = mw.getPrior(20)
        mw.showWrapperLines(next);        
		next = mw.getPrior(10)
        mw.showWrapperLines(next);        

		println "asking for last 10 lines using default getLast()"
		next = mw.getLast()
        mw.showWrapperLines(next);        

		println "asking for last3 lines using getLast(3)"
		next = mw.getLast(3)
        mw.showWrapperLines(next);        

		println "asking for last 2 lines using default getLast(2) to see if returned same as above"
		next = mw.getLast(2)
        mw.showWrapperLines(next);        

		println "asking for first 10 lines using default getFirst()"
		next = mw.getFirst()
        mw.showWrapperLines(next);        

		println "asking for first 2 lines using getFirst(2)"
		next = mw.getFirst(2)
        mw.showWrapperLines(next);        

		println "asking for last 3 lines to see if 'current=${mw.current} is positioned ok after getFirst(2)"
		next = mw.getLast(3)
        mw.showWrapperLines(next);        



        println "... the end "
        println "--------------------------------------------------"
    } // end of main
 }     // end of class    