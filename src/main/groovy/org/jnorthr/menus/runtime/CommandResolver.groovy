 // start "" "http://superuser.com" on windows 7 - runs default browser
package org.jnorthr.menus.runtime;

public class CommandResolver
{
    CommandResponse cr;
    
    // load handle to response object
    public CommandResolver(CommandResponse cr)
    {
        this.cr = cr;
    }  // end of constructor
    
    public resolveCommand()
    {
        String word1LC = cr.word1LC; 
        def commands = ["go":1,"open":2,"run":3,"start":4,"command":5,"sh":6,"cd":7,"pwd":8,"echo":9,"ls":10,"dir":11]

        def found = (commands[word1LC]) ? true : false;
        def task = (found) ? commands[word1LC] : -1;
        
        say "<${word1LC}> found=$found task=$task"


        switch ( task )
        {
            case 1..99 :    cr.internalcommand = true;
                            break;
                            
            default :       say "... default for "+word1LC        
        } // end of switch
        
        // -1 if unknown, 0=yes, we know but no action
        return task;
    } // end of resolve
    
    
    public say(tx) { println tx;}
    
    // --------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "... started"
        CommandResolver cr = new CommandResolver()    
        cr.resolveCommand("go");
        cr.resolveCommand("xxx");
        cr.resolveCommand("44");
        cr.resolveCommand("pwd");
        println "... ended"
    } // end of main

} // end of CommandResolver.class
