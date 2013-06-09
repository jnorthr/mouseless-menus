package org.jnorthr.menus.support;

/* 
 * A color conversion support utility. Changes hex-to-color names and/or lowercase color names to hex
 */ 
public class ClassLoaderManager
{
    List<String> classpathEntries = []
    
    // ===============================================
    // default constructor loads internal color tables
    public ClassLoaderManager()
    {
        printClassPath this.class.classLoader;
    }    // end of class constructor


    public printEntries()
    {
        classpathEntries.each{e -> println e; }
    } // end of method
    

    public printClassPath(classLoader) 
    {
          classpathEntries << "classLoader.parent: $classLoader"
          classLoader.getURLs().each 
          { url->
             classpathEntries << "- ${url.toString()}"
          } // end of each
          if (classLoader.parent) 
          {
              printClassPath(classLoader.parent)
          } // end of if
          
    } // end of method

    // -----------------------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "--------------------------------------------------"
        println "... started"
        ClassLoaderManager mf = new ClassLoaderManager();
        println "";
        mf.printEntries();
        println "";
        println "... the end "
        println "--------------------------------------------------"

    } // end of main
} // end of class
