package org.jnorthr.menus.support;
import org.jnorthr.menus.support.ColorManager;
import java.awt.Color;

/* 
 * A validation utility. Confirm certain things about a string
 */ 
public class Validator
{
    // show/hide audit trail msgs
    boolean audit = false;

    // has a remark/comment 
    boolean remarks = false;

    // not a remark/comment and has valid menu signature  :=
    boolean valid = false;

    // index of semi-colon in string
    int semiIndex = -1;

    // index of := in string
    int menuSignatureIndex = -1;


    // String with text component if found; whats to the left of the :=
    // commonly used as the name of a menu entry
    String textComponent = "";

    // String with command component if found; whats to the right of the :=
    // commonly holds the logic or command to execute when this menu entry is selected
    String commandComponent = "";


    // color signature  ;   found only if valid and !remarks
    boolean hasColor = false;

	// when hasColor, the following fields are populated ----->
    // String with color component if found
    String colorComponent = "";


    // integer equivalent of colorComponent
    int colorInteger = 0;

    // internally maintained in synch with fontcolor; 
    java.awt.Color textColor = new java.awt.Color(0);	


    // ===============================================
    // constructor determines validity of string
    public Validator(String ln)
    {
		say ln;
		remarks = hasRemark(ln);

		if (!remarks)
		{	
	    	hasColor = hasSemi(ln)
	    	valid = isValid(ln)

	    	if (valid)
	    	{
	        	menuSignatureIndex = getSignatureIndex(ln)
				textComponent = getTextComponent(ln);
				commandComponent = getCommandComponent(ln)
	    	} // end of if
	    	else
	    	{
				textComponent = ln;
	    	} // end of else

	    	// divide string into components
		    if (hasColor)
		    {
	    	    semiIndex = getSemiIndex(ln)
				colorComponent = getColorComponent(ln)
				textComponent  = getNewTextComponent(textComponent);
				def cm = new ColorManager(colorComponent);
				colorComponent = cm.getHexCode();
				colorInteger = cm.getColorCode();
				textColor = new Color(colorInteger);
	    	} // end of if

		    colorComponent = colorComponent.trim()
		    textComponent = textComponent.trim()
	    	commandComponent = commandComponent.trim();
		} // end of not a remark

    }    // end of class constructor




    // =========================================
    // show audit trail if allowed
    public say(tx)
    {
        if (audit) 
    	{
            println tx;
    	} // end of if
    }    // end of method



    // =========================================
    // confirm menu item signature of :=
    public boolean isValid(String tx)
    {
	// disallow comment & remarks lines
	int j = tx.indexOf(":=");
	return ( j < 2  ) ? false : true ;	
    }   // end of method


    // =========================================
    // confirm comment or remarks signature
    public boolean hasRemark(String tx)
    {
	// disallow comment & remarks lines
	int j = tx.indexOf("//");
	return ( j < 0 || j > 4 ) ? false : true ;	
    }   // end of method


    // =========================================
    // see if semi-colon
    public boolean hasSemi(String text)
    {
        // find first semi-colon ;
        def at =  text.indexOf(';') 
        return (at < 0 || at > 17) ? false : true; 
    } // end of hasSemi


    // =========================================
    // get index to semi-colon
    public int getSemiIndex(String text)
    {
        return text.indexOf(';'); 
    } // end of getSemiIndex 

    // =========================================
    // get index to :=
    public int getSignatureIndex(String text)
    {
        return text.indexOf(":="); 
    } // end of getSignatureIndex



    // =========================================
    // get first portion of input string
    public getTextComponent(String text)
    {
        return text.substring(0, menuSignatureIndex); 
    } // end of method

   // =========================================
    // get following portion of input string
    public getCommandComponent(String text)
    {
        return text.substring(menuSignatureIndex + 2); 
    } // end of method

    // =========================================
    // get color declartion of first portion of input string
    public getColorComponent(String text)
    {
        return text.substring(0, semiIndex); 
    } // end of method

    // =========================================
    // get first portion of input string without the color
    public getNewTextComponent(String text)
    {
        return text.substring(semiIndex+1); 
    } // end of method


    // class toString() method
    String toString() 
    { 
        return "remarks=<${remarks}> valid=<${valid}> hasColor=<${hasColor}> semiIndex=<${semiIndex}> menuSignatureIndex=<${menuSignatureIndex}> colorComponent=<${colorComponent}> colorInteger=<${colorInteger}> textColor=<${textColor}> textComponent=<${textComponent}> commandComponent=<${commandComponent}>"
    }    // end of method


    // -----------------------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "--------------------------------------------------"
        println "... started"
        Validator mf = new Validator("#336699;Hi Kids !:=groovy -v");
        println mf;
        println "";


        mf = new Validator(" // #336699;Hi Kids !:=groovy -v");
        println mf;
        println "";

        mf = new Validator("#336699;Hi Kids !");
        println mf;
        println "";

        mf = new Validator("Hi Kids !:=groovy -v");
        println mf;
        println "";

        mf = new Validator("Hi Kids !");
        println mf;
        println "";

        mf = new Validator("  ");
        println mf;
        println "";


        mf = new Validator("Fred Flintstone:=*menutitle");
        println mf;
        println "";

        mf = new Validator("  #336699;   Hi Kids !   :=   groovy -v   ");
        println mf;
        println "";

        mf = new Validator("darkred;");
        println mf;
        println "";

        mf = new Validator("    darkred;  darkred");
        println mf;
		println mf.textComponent;
        println "";

        println "... the end "
        println "--------------------------------------------------"

    }    // end of main
} // end of class