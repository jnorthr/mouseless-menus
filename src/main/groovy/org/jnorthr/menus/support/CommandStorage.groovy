// handles stack of commands that have been keyed by user, both push and pop;
// base stack entry is always the first entry on stack and is blank

// this module should replace the MenuSupport features
package org.jnorthr.menus.support;
class CommandStorage
{
    boolean audit = false

    def stack = []
    def lookback = 0 // really just stack size

    // cook your own class toString
    public String toString()
    {
        String tx = "lookback=$lookback stack.size()=${stack.size()}"
        return tx;    
    } // end of toString()


    // print debug text (maybe)
    public void say(def text) 
    {
        if (audit) {println "$text";} 
    } // end of say


    // class constructor - loads configuration,etc.
    public CommandStorage()
    {
    } /// end of constructor


    // non-distructive retrieve prior command
    public getPriorCommand()
    {    
        def value = stack[ --lookback ];

        if (lookback < 1) 
        { 
        	resetStack(); 
			java.awt.Toolkit.getDefaultToolkit().beep();
        } // end of if

        say "getPriorCommand() stack.size()=${stack.size()} lookback=$lookback <${value}>" 

        return value
    } // end of getPriorCommand

    // non-distructive retrieve next command
    public getNextCommand()
    {    
        if ( lookback > stack.size() - 2 ) 
        {
            lookback = -1;
            java.awt.Toolkit.getDefaultToolkit().beep();
        } 

    	def value = stack[ ++lookback ];
        say "getNextCommand() stack.size()=${stack.size()} lookback=$lookback <$value>" 

        return value
    } // end of getNextCommand


    // non-distructive retrieve of most recent command
    public getLatestCommand()
    {    
        resetStack();
        return stack[stack.size() - 1]
    } // end of getLatestCommand



    // set lookback pointer to last command in the stack
    public resetStack()
    {
        lookback = stack.size();
    } // end of reset


    // F9&UP arrow lookback thru commands
    public putStack(def entry)
    {
    	say "putStack($entry)"
        stack << entry;
        resetStack();
    } // end of getStack
    

    // test harness for this class
    public static void main(String[] args)
    {    
        println "--- starting ..."
        CommandStorage s = new CommandStorage();
        println s;

        s.resetStack();
        println s;

        s.putStack("cd fred")
        println s;

        def va = s.getLatestCommand()
        println "getLatestCommand()="+va+ " :"+s;
        
        va = s.getPriorCommand()
        println "getPriorCommand()="+va+ " :"+s;

        va = s.getPriorCommand()
        println "getPriorCommand()="+va+ " :"+s;

        s.putStack("hi kids")
        println s;

        va = s.getLatestCommand()
        println "getLatestCommand()="+va+ " :"+s;

        va = s.getPriorCommand()
        println "getPriorCommand()="+va+ " :"+s;

        va = s.getPriorCommand()
        println "getPriorCommand()="+va+ " :"+s;

        va = s.getPriorCommand()
        println "getPriorCommand()="+va+ " :"+s;


        println "--------------------------\n--- ending ..."
    } // end of main
    

} // end of class