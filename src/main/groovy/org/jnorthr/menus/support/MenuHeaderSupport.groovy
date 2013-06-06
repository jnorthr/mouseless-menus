// utility class to build text panes of menu items to mimic multiple columns; see HeaderSupportTest.groovy for test harness 
package org.jnorthr.menus.support;
import groovy.swing.SwingBuilder
import javax.swing.*
import java.awt.*
import javax.swing.text.*;
import org.jnorthr.menus.support.PropertyHarvester;

// the onscreen representation of one document pane            
public class HeaderSupport extends JTextPane
{
    StyledDocument doc;            	// a storage repository for text
    static SimpleAttributeSet as1;      // text decorations to apply to text within 'doc'
    static audit = false			// flag to turn audit messages to console
    PropertyHarvester cs;


    // retrieve  jtp text pane styled with colors to be used as the menu
    public JTextPane getColumn()
    {
        return  this;
    } // end of getColumn
    

    // print debug text (maybe)
    public static void say(def text) 
    {
        if (audit) {println text;} 
    } // end of say


    // originally had menu options and commands as part of properties file but now these are found in their own .txt file
    // this method reloads them and divides 1st third into left column, the remainder into the middle and right columns
    // mifilename is name of menu file to load
    public loadMenu(columns,menuitems) 
    {  

		say "loadMenu..."

       	int numberofcolumns = columns.size()	// how many columns this time ?
        def itemflag = [numberofcolumns]
        def itemcount = [numberofcolumns]

        // =====================================================================================================
       	// walk thru each title and place first third of menu items in column 1 and 2 with remainder in column 3
        //int i = 0;

        // there are enough menu items to divide between columns
        int percolumn = menuitems.size() / numberofcolumns
		say "loadMenu... columns:"+numberofcolumns+" m/i count:"+menuitems.size()+" percolumn:"+percolumn

        if (percolumn * numberofcolumns != menuitems.size())     // if not modulo three
        {
        	percolumn+=1;
            say "not even number of items per col: $percolumn";
        } // end of if

        // erase each menu item column, set flags true and items/per/column count ot zero
		numberofcolumns.times
		{ ix -> 
			itemflag[ix] = true;
			itemcount[ix] = percolumn;
		} // end of each

        columns.each { it.clearColumnText(); }

        say("there are ${menuitems.size()} menu items of $percolumn items per column in $numberofcolumns columns")
		say("   the max entries for this table is ${percolumn * numberofcolumns}")

        int thiscolumn = 0
        def val
		String tx
		String whole
		int x = 0

        // now roll thru the titles and assign them to each of the columns
        menuitems.eachWithIndex
        {   it, ix ->   

			say "\nmenuitem <$it> and ix=$ix "
            thiscolumn = ix / percolumn;    // compute a column number for this menu item to appear in

			tx = it.key.trim() 

			// see if more than one environment var. name is present, i.e. comma is in map value.
			// like "User :":"user,uid",  from menu.properties; so map key User : points to map value "user,uid"
			// requiring two values from PropertyHarvester map loaded at boj. Resulting in User : jim 502


			// only need a single text value as map value is a single key to look up an environmental value
	       	if ( it.value.indexOf(",") < 1) 
			{ 
				val = " "+cs.getMap(it.value.trim()) 
			}

			// else there are two (but not more than are coded for) text values to place in header column of menu
			else  
			{
				def tokens = it.value.trim().split(",")
				val = " "+cs.getMap(tokens[0].trim()) 
				val += " "+cs.getMap(tokens[1].trim()) 
			} // end of else

			int pad = 0
			if ( thiscolumn.equals(1) )
			{ 
				if ( val.size() + x < 40) 
				{ 
					int p = 40 - ( val.size() + tx.size() ) 
					pad = (p < 1) ? 0 : p / 2;
				} // end of if
			} // end of if

			if ( pad < 1 )
			{
				whole = tx
			}
			else 
			{	
				whole = ""
				pad.times{ whole += ' '; }
				whole += tx
			} // end of else

	    	// if this is the first time thru for this column, erase the leading c/r
			( itemflag[thiscolumn] )  ?  itemflag[thiscolumn] = false  :  columns[thiscolumn].appendColumnText('\n')
        	columns[thiscolumn].appendColumnText(whole+val)    // stuff in column 1

        } // end of walking thru each title 

	    // find how many blank lines go in final column
        int j = ( ( thiscolumn + 1 ) * percolumn )            

        // add extra blank m/i entries to make last column look nicer on odd m/i count
        j.times{ columns[numberofcolumns-1].appendColumnText("\n") }
    
    } // end of loadMenu


    // ============================================
    // class constructor - loads configuration,etc.
    public HeaderSupport(PropertyHarvester comset)
    {
		// get link to PropertyHarvester
		cs = comset;
		
        // use white text    
        as1 = new SimpleAttributeSet()
        StyleConstants.setForeground(as1, Color.white);
		StyleConstants.setAlignment(as1 , StyleConstants.ALIGN_RIGHT);
        //StyleConstants.setBold(as1, true);
        //StyleConstants.setItalic(as1, true);

        doc = new DefaultStyledDocument()
        setStyledDocument (doc);
        setBackground(Color.black);
        setFocusable(false)
        setEditable(false);
        setFont(new Font("Monospaced", Font.PLAIN, 10));
        setMinimumSize(new Dimension(260,80));
        setPreferredSize(new Dimension(260,150));
        setMaximumSize(new Dimension(500,200));
        setBorder(null)
    } // end of constructor


    // ============================================
    // This is logic to populate the joblog of the menu panel =========================
    // Clear out current document
    private void clearColumnText() 
    {
		doc = new DefaultStyledDocument();
        setStyledDocument (doc);  // su.getColumn()
    } // end of clearColumnText



    // ============================================
    // method to add text to column pane with specific display attributes
    private void appendColumnText(String s) 
    {
        doc = this.getStyledDocument()             
        try 
        {
            doc.insertString(doc.getLength(), s, as1);
        }
        catch (BadLocationException e) {}
    } // end of appendColumnText


    // ============================
    // test harness for this class
    public static void main(String[] args)
    {    
        println "---> starting"

		PathFinder resourcePath = new PathFinder(); 
		def config = resourcePath.menuMap;		

        def cs = new PropertyHarvester()
		HeaderSupport hs = new HeaderSupport(cs);
		
		def pane = hs.getColumn()
		pane.clearColumnText()
		pane.appendColumnText("Hi kids")
		
		java.util.List<HeaderSupport> columns = []
		columns << pane;

		HeaderSupport hs2 = new HeaderSupport(cs);
		pane = hs2.getColumn()
		pane.clearColumnText()
		pane.appendColumnText("Freddie was here\nand not too soon, either.")
		columns << pane;

		HeaderSupport hs3 = new HeaderSupport(cs);
		pane = hs3.getColumn()
		pane.clearColumnText()
		pane.appendColumnText("Freddie was here but left early for another show\nand not too soon, either as his whole band had left for the party\nwithout him.")
		columns << pane;

		hs.loadMenu(columns,config.itemtitles)

		
		JFrame.setDefaultLookAndFeelDecorated(true);
		def swing = new SwingBuilder()
		def frame = swing.frame(title:"HeaderSupport", background:Color.black, pack:true, show:true, 
			defaultCloseOperation:JFrame.EXIT_ON_CLOSE, minimumSize:[300, 80], preferredSize:[800, 150]) 
		{
				hbox()   //minimumSize:[200, 300])
				{
					columns.each{widget(it)}
				}
		}

        println "... end"
    } // end of main


} // end of HeaderSupport.class