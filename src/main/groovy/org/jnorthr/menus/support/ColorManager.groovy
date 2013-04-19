package org.jnorthr.menus.support;
import java.awt.Color;

/* 
 * A color conversion support utility. Changes hex-to-color names and/or lowercase color names to hex
 */ 
public class ColorManager
{
	// show/hide audit trail msgs
	boolean audit = false;
	
	// filled in at constructor
	def colors = [:]		// map of color names
	private String text="";	// trimmed & lowercase input string
	public boolean hasCode = false;

	// Filled in when/if a color code or name was found at start of text string
	// code;item
	// #c00080;Some Text
	private String code=""; // what's left  of input string to the left of the leading ; if any
	private String item="";	// what's right of input string to the right of the leading ; if any

	// the hexidecimal representation of the value to left of ; from 'code' above
	private String hexcode="";	

	// the numeric integer equiv. of hexcode
    private int colorcode=-1;	


	// 1 arg string constructor like : "0x336699;Declare normal color sig 0x336699; with semicolon."
	public ColorManager(String tx)
	{
		loadColorArray();
			
		text= tx.trim();
        hasCode = getWord(text);
	}	// end of method

    // =========================================
    // show audit trail if allowed
    public say(tx)
    {
    	if (audit) 
		{
			println tx;
		} // end of if
    }	// end of method

    // =========================================
    // insert each html color name plus it's hex equivalent ;
    public loadColorArray()
    {
		colors += [ "aliceblue":"F0F8FF"]
		colors += [ "antiquewhite":"FAEBD7"]
		colors += [ "aqua":"00FFFF"]
		colors += [ "aquamarine":"7FFFD4"]
		colors += [ "azure":"F0FFFF"]
		colors += [ "beige":"F5F5DC"]
		colors += [ "bisque":"FFE4C4"]
		colors += [ "black":"000000"]
		colors += [ "blanchedalmond":"FFEBCD"]
		colors += [ "blue":"0000FF"]
		colors += [ "blueviolet":"8A2BE2"]
		colors += [ "brown":"A52A2A"]
		colors += [ "burlywood":"DEB887"]
		colors += [ "cadetblue":"5F9EA0"]
		colors += [ "chartreuse":"7FFF00"]
		colors += [ "chocolate":"D2691E"]
		colors += [ "coral":"FF7F50"]
		colors += [ "cornflowerblue":"6495ED"]
		colors += [ "cornsilk":"FFF8DC"]
		colors += [ "crimson":"DC143C"]
		colors += [ "cyan":"00FFFF"]
		colors += [ "darkblue":"00008B"]
		colors += [ "darkcyan":"008B8B"]
		colors += [ "darkgoldenrod":"B8860B"]
		colors += [ "darkgray":"A9A9A9"]
		colors += [ "darkgreen":"006400"]
		colors += [ "darkkhaki":"BDB76B"]
		colors += [ "darkmagenta":"8B008B"]
		colors += [ "darkolivegreen":"556B2F"]
		colors += [ "darkorange":"FF8C00"]
		colors += [ "darkorchid":"9932CC"]
		colors += [ "darkred":"8B0000"]
		colors += [ "darksalmon":"E9967A"]
		colors += [ "darkseagreen":"8FBC8F"]
		colors += [ "darkslateblue":"483D8B"]
		colors += [ "darkslategray":"2F4F4F"]
		colors += [ "darkturquoise":"00CED1"]
		colors += [ "darkviolet":"9400D3"]
		colors += [ "deeppink":"FF1493"]
		colors += [ "deepskyblue":"00BFFF"]
		colors += [ "dimgray":"696969"]
		colors += [ "dodgerblue":"1E90FF"]
		colors += [ "firebrick":"B22222"]
		colors += [ "floralwhite":"FFFAF0"]
		colors += [ "forestgreen":"228B22"]
		colors += [ "fuchsia":"FF00FF"]
		colors += [ "gainsboro":"DCDCDC"]
		colors += [ "ghostwhite":"F8F8FF"]
		colors += [ "gold":"FFD700"]
		colors += [ "goldenrod":"DAA520"]
		colors += [ "gray":"808080"]
		colors += [ "green":"008000"]
		colors += [ "greenyellow":"ADFF2F"]
		colors += [ "honeydew":"F0FFF0"]
		colors += [ "hotpink":"FF69B4"]
		colors += [ "indianred":"CD5C5C"]
		colors += [ "indigo":"4B0082"]
		colors += [ "ivory":"FFFFF0"]
		colors += [ "khaki":"F0E68C"]
		colors += [ "lavender":"E6E6FA"]
		colors += [ "lavenderblush":"FFF0F5"]
		colors += [ "lawngreen":"7CFC00"]
		colors += [ "lemonchiffon":"FFFACD"]
		colors += [ "lightblue":"ADD8E6"]
		colors += [ "lightcoral":"F08080"]
		colors += [ "lightcyan":"E0FFFF"]
		colors += [ "lightgoldenrodyellow":"FAFAD2"]
		colors += [ "lightgreen":"90EE90"]
		colors += [ "lightgrey":"D3D3D3"]
		colors += [ "lightpink":"FFB6C1"]
		colors += [ "lightsalmon":"FFA07A"]
		colors += [ "lightseagreen":"20B2AA"]
		colors += [ "lightskyblue":"87CEFA"]
		colors += [ "lightslategray":"778899"]
		colors += [ "lightsteelblue":"B0C4DE"]
		colors += [ "lightyellow":"FFFFE0"]
		colors += [ "lime":"00FF00"]
		colors += [ "imegreen":"32CD32"]
		colors += [ "linen":"FAF0E6"]
		colors += [ "magenta":"FF00FF"]
		colors += [ "maroon":"800000"]
		colors += [ "mediumauqamarine":"66CDAA"]
		colors += [ "mediumblue":"0000CD"]
		colors += [ "mediumorchid":"BA55D3"]
		colors += [ "mediumpurple":"9370D8"]
		colors += [ "mediumseagreen":"3CB371"]
		colors += [ "mediumslateblue":"7B68EE"]
		colors += [ "ediumspringgreen":"00FA9A"]
		colors += [ "mediumturquoise":"48D1CC"]
		colors += [ "mediumvioletred":"C71585"]
		colors += [ "midnightblue":"191970"]
		colors += [ "mintcream":"F5FFFA"]
		colors += [ "mistyrose":"FFE4E1"]
		colors += [ "moccasin":"FFE4B5"]
		colors += [ "navajowhite":"FFDEAD"]
		colors += [ "navy":"000080"]
		colors += [ "oldlace":"FDF5E6"]
		colors += [ "olive":"808000"]
		colors += [ "olivedrab":"688E23"]
		colors += [ "orange":"FFA500"]
		colors += [ "orangered":"FF4500"]
		colors += [ "orchid":"DA70D6"]
		colors += [ "palegoldenrod":"EEE8AA"]
		colors += [ "palegreen":"98FB98"]
		colors += [ "paleturquoise":"AFEEEE"]
		colors += [ "palevioletred":"D87093"]
		colors += [ "papayawhip":"FFEFD5"]
		colors += [ "peachpuff":"FFDAB9"]
		colors += [ "peru":"CD85"]
		colors += [ "pink":"FFC0CB"]
		colors += [ "plum":"DDA0DD"]
		colors += [ "powderblue":"B0E0E6"]
		colors += [ "purple":"800080"]
		colors += [ "red":"FF0000"]
		colors += [ "rosybrown":"BC8F8F"]
		colors += [ "royalblue":"4169E1"]
		colors += [ "saddlebrown":"8B4513"]
		colors += [ "salmon":"FA8072"]
		colors += [ "sandybrown":"F4A460"]
		colors += [ "seagreen":"2E8B57"]
		colors += [ "seashell":"FFF5EE"]
		colors += [ "sienna":"A0522D"]
		colors += [ "silver":"C0C0C0"]
		colors += [ "skyblue":"87CEEB"]
		colors += [ "slateblue":"6A5ACD"]
		colors += [ "slategray":"708090"]
		colors += [ "snow":"FFFAFA"]
		colors += [ "springgreen":"00FF7F"]
		colors += [ "steelblue":"4682B4"]
		colors += [ "tan":"D2B48C"]
		colors += [ "teal":"008080"]
		colors += [ "thistle":"D8BFD8"]
		colors += [ "tomato":"FF6347"]
		colors += [ "turquoise":"40E0D0"]
		colors += [ "violet":"EE82EE"]
		colors += [ "wheat":"F5DEB3"]
		colors += [ "white":"FFFFFF"]
		colors += [ "whitesmoke":"F5F5F5"]
		colors += [ "yellow":"FFFF00"]
		colors += [ "yellowGreen":"9ACD32"]   
	}	// end of method


    // =========================================
    // see if semi-colon
    public int hasSemi(String text)
    {
        // find first semi-colon ;
        def at =  text.indexOf(';') 
        switch(at)
        {
            case -1 : break;
            case 0..17 : break;
            default : at = -1;        
        }	// end of switch

        return at;
    } // end of hasSemi

    // =========================================
    // return whats left of text after color code is removed
    public getText()
    {
    	return text;
    }	// end of method

    // =========================================
    // return color code what's on left-side of text as a String
    public getCode()
    {
    	return code;
    }	// end of method

    // =========================================
    // return what's on right-side of ; as text as a String
    public getItem()
    {
    	return item;
    }	// end of method

    // =========================================
    // return whats left of text after color code is removed as an integer
    public int getColorCode()
    {
    	return colorcode;
    }	// end of method

    // =========================================
    // return whats left of text after color code is removed as a String
    public getHexCode()
    {
    	return hexcode;
    }	// end of method

    // =========================================
    // find word before semicolon like #c00080; = #c00080
    public boolean getWord(String wd)
    {
    	hexcode = null;
    	colorcode = -1;

        // find first semi-colon ; or return if none found
        def at = hasSemi(wd); 
		say "\nat="+at
        if (at<0)	return false;


		// fill 'code' with either the color name or some hex value as a string
		code = wd.substring(0,at).trim().toLowerCase();
		say "code="+code
		boolean ok = getHexColor(code);
		say "ok="+ok+" hexcode="+hexcode

		if (!(ok) || hexcode.size() < 2) 
		{
			hexcode=null;
			return false;
		}	// end of method

		// double up hex, so #abc  becomes #aabbcc
		if (hexcode.size() == 3)
		{
			def hc="";
			hexcode.each{c -> hc+=c; hc+=c; }
			hexcode =hc;
		}	// end of if

		// here, the value in hexcode is pure hexidecimal, so we convert to integer equiv.
		colorcode = decodeHexColor(hexcode);
		say "colorcode="+colorcode
		if (colorcode<0) 
		{
			hexcode=null;
			return false;
		}
		else
		{
			// convert of hex to integer went ok, so we fill 'item' with all text to right of ;
			item = text.substring(at+1)
			say "item="+item
		}

        return true;
    } // end of getWord



    // =========================================
    // find color signature from 0xcc88aa  or #cc88aa or a color name like 'darkred'
	// a color signature could appear before the first semi ; in a text string like any of these choices :
	// 0xcc88aa;Some Text
	// #cc88aa;Some Text
	// 0xDarkRed;Some Text
	// darkred;Some Text
    public getHexColor(word)
    {
    	hexcode=null;
 		say "getHexColor(${word})"

		if (word.size() < 3 )
		{
 			return false;
		}	// end of if

		if (word.substring(0,1)=='#')
		{
			word = word.substring(1)
		}	// end of if
 		
		if (word.substring(0,2)=='0x')
		{
			word = word.substring(2)
		}	// end of if
		
		say "now getHexColor(${word})"

 		// so 0xcc88aa comes out as cc88aa & #cc88aa comes out as cc88aa and cornflowerblue comes out as 6495ed
		if (colors.containsKey(word))
		{
			hexcode = colors[(word)]
			say "... colors.containsKey($word) giving ($hexcode)"
		}	// end of if
		else
		{
			hexcode = word;
			say "...failed to find color name ($word)"
		}	// end of else

        return true;
    } // end of getSignature


    // logic to yield an Integer from a hex code
    public decodeHexColor(hexcode)
    {
    	def i = 0;
    	try 
        {
	 		i = Integer.parseInt(hexcode,16);
	 		//Integer.decode(hex);
        }
        catch (NumberFormatException e) { i = -1; }

        return i;

    }	// end of method


    // class toString() method
	String toString() 
	{ 
		return "text=<$text> hasCode=<${hasCode}> item=<${getText()}> code=<$code> hexcode=<${getHexCode()}> colorcode=<${getColorCode()}>"
	}	// end of method


	// -----------------------------------------------------
	// test harness for this class
	public static void main(String[] args)
	{	
		println "--------------------------------------------------"
		println "... started"
		ColorManager mf = new ColorManager("#336699;Hi Kids !");
		println mf;

		if (args.size() > 0)
		{
			mf = new ColorManager(args[0]);
			println mf;
		} // end of if
		
		mf = new ColorManager("0x336699;Declare normal color signature 0x336699; with semicolon.");
		println mf;

		mf = new ColorManager("0x369;Declare normal color signature 0x369; with semicolon.");
		println mf;

		mf = new ColorManager("#336699;Declare normal color signature #336699; with semicolon.");
		println mf;

		mf = new ColorManager("darkred;Declare normal color signature darkred; with semicolon.");
		println mf;

		mf = new ColorManager("DarkBlue;Declare normal color signature darkblue; with semicolon.");
		println mf;

		mf = new ColorManager("#c00080;Declare normal color signature #c00080; with semicolon.");
		println mf;

		mf = new ColorManager("#c00080;Declare normal color signature #c00080; with semicolon.");
		println mf;


		println "-------------\nFailure trials follow :\n"
		mf = new ColorManager("#336699 Declare normal color signature #336699 with no semicolon.");
		println mf;

		mf = new ColorManager("darkblack;Declare unknown color signature darkblack; with semicolon.");
		println mf;

		mf = new ColorManager("#6t55rr;Declare bad color signature darkblack; with semicolon.");
		println mf;

		mf = new ColorManager("c00080; Declare normal color signature c00080; with semicolon but no #.");
		println mf;

		mf = new ColorManager("fred; was; here;");
		println mf;


		println "... the end "
		println "--------------------------------------------------"

	}	// end of main
} // end of class