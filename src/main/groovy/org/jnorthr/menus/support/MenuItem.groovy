// utility class to confirm a line of text has signal character pattern of :=
package org.jnorthr.menus.support;
 
public class MenuLine
{ 
     public boolean audit = true;
     public String line = "";
     def menutext = ""
     def menucommand = ""
     def phrase = ""
     
     public say(tx) { if (audit) println tx;}
     
     // default constructor
     public MenuLine()
     {
         line = "";
     } // end of constructor
     
     
     // non-default constructor
     public MenuLine(String line)
     {
         this.line = line;
     } // end of constructor
     

     // 
     public boolean isMenuLine()
     {  
         return isMenuLine(this.line)
     } // end of method

     // look for a search pattern
     public boolean hasText(String find)
     {  
         def ans = false;
         int ix1 = 0;
         
         def search = find.trim().toLowerCase();
         if (search.size() < 1) return false;
         
         def terms = search.split(" ")    // break search term into a series of parts
         println "   hasText($search)=<"+phrase+">; and has ${terms.size()} individual terms"
         
         terms.each{term->
             if (term.trim()!=" ")
             {             
                 ans = (phrase =~ /${term}/) ? true: false;
                 if (ans) ix1+=1;
             } // end of if
                     
         } // end of each
         
         ans = (ix1 == terms.size() ) ? true: false;         
         return ans;
     } // end of method

     
     /*
     *  determine of a line of text has the menu item signal character pattern of :=
     */
     public boolean isMenuLine(def aline)
     {  
        say "-> isMenuLine($aline)"
         
        // code to ignore comment lines
        // true when this line is a possibility to hold :=
        def useme = true        
        int ix2 = aline.trim().indexOf("//")

        switch (ix2) 
        {
            case 0..3 :    useme = false;
                           break;
        } // end of switch

        if (!useme)
        {
            say "   comment line ignored:<$aline>"
            return false;
        } // end of if


        // if not a comment and line has := then split
        if (!(aline.trim() =~ /^.*\:=.*/))         
        {
            say "   non-signature line ignored:<$aline>"
            useme = false;
        } // end of if

        if (useme)
        {
            def wds = aline.trim().split(/\:=/)
            if (wds.size() < 2 )   
        } //end of if
        
        return useme;         
     } // end of method
         

     /*
     *  split a line of text using signal character pattern of :=
     */
     public splitMenuLine()
     {  
        return splitMenuLine(this.line)
     } // end of method



     /*
     *  split a line of text using signal character pattern of :=
     */
     public boolean splitMenuLine(def aline)
     {  
        say "-> splitMenuLine($aline)"
         
        def words = aline.trim().split(/\:=/)    // break menu entry into 2 parts: 1) option text description 2) option command
        int wc = words.size()
        menutext = ""
        menucommand = ""
        phrase = ""
                
        // set true when the command pair form a valid command
        boolean flag = false    

        // word count governs how it's handled
        switch (wc)        
        {
            // typical menu item line of text:=command
            case 2:        
                    menutext = words[0].trim()
                    menucommand=words[1].trim()
                    if ( menutext.size() > 0 && menucommand.size() > 0  )
                    {
                        flag = true
                        phrase = menutext.trim().toLowerCase()+" "+menucommand.trim().toLowerCase()
                    } // end of if
                    break;

            default:
                    say "unknown sequence for line <${aline}>"
                    break;
             } // end of switch

        return flag;
     } // end of method
                
                

    // test harness for this class
    public static void main(String[] args)
    {    
        println "... started"
        
        boolean ok = true;
        String finder = "fred"

        MenuLine ml = new MenuLine();
        String one = "hi kids:=echo 'hi kids'";
        boolean result = ml.isMenuLine(one);
        println "   isMenuLine(${one}) = "+result
        if (result)
        {
            ok = ml.splitMenuLine(one)
            println "   splitMenuLine() = "+ok
            if (ok)
            {
                println "   menutext=<$ml.menutext>\n   menucommand=<$ml.menucommand>"  
                finder = " ctor default"  
                ok = ml.hasText(finder) 
                println "   ml.hasText(${finder}) = $ok"  
            } // end of if
        } // end of if
        
        println "\n"
                
        one = "// this is a comment";
        result = ml.isMenuLine(one);
        println "   isMenuLine(${one}) = "+result
        if (result)
        {
            ok = ml.splitMenuLine(one)
            println "   splitMenuLine() = "+ok
            if (ok)
            {
                println "   menutext=<$ml.menutext>\n   menucommand=<$ml.menucommand>"   
                finder = " ctor default" 
                ok = ml.hasText(finder) 
                println "   ml.hasText(${finder}) = $ok"  
            } // end of if
        } // end of if
        println "\n"
        
        one = "";
        result = ml.isMenuLine(one);
        println "   isMenuLine(${one}) = "+result
        if (result)
        {
            ok = ml.splitMenuLine(one)
            println "   splitMenuLine() = "+ok
            if (ok)
            {
                println "   menutext=<$ml.menutext>\n   menucommand=<$ml.menucommand>"   
                finder = " ctor default" 
                ok = ml.hasText(finder) 
                println "   ml.hasText(${finder}) = $ok"  
            } // end of if
        } // end of if
        println "\n"


        one = "use non default constructor:=echo 'hi kids'";
        ml = new MenuLine(one);
        result = ml.isMenuLine();
        println "   isMenuLine(${one}) = "+result
        if (result)
        {
            ok = ml.splitMenuLine()
            println "   splitMenuLine() = "+ok
            if (ok)
            {
                println "   menutext=<$ml.menutext>\n   menucommand=<$ml.menucommand>" 
                finder = " ctor default" 
                ok = ml.hasText(finder) 
                println "   ml.hasText(${finder}) = $ok"
                finder = "  non USE"  
                ok = ml.hasText(finder) 
                println "   ml.hasText(${finder}) = $ok"  
                
                finder = "  non USE Kids"  
                ok = ml.hasText(finder) 
                println "   ml.hasText(${finder}) = $ok"  
                
                finder = "  "  
                ok = ml.hasText(finder) 
                println "   ml.hasText(${finder}) = $ok"  
            } // end of if

        } // end of if

        println "\n"

        one = "use non default constructor:=";
        ml = new MenuLine(one);
        result = ml.isMenuLine();
        println "   isMenuLine(${one}) = "+result


        println "... the end ..."
    } // end of main
    
} // end of class   