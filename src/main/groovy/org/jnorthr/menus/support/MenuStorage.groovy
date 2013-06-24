// handles stack of menu names, both push and pop;
// base menu is always the lowest entry on stack and cannot be removed
package org.jnorthr.menus.support;
class Storage
{
	List<String> stackMenu = []
	boolean audit = false
	
	// PathFinder will give us the full folder name, we add .txt
	def mainmenufilename = "main"

	// print debug text (maybe)
	public void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of say


	// class constructor - loads configuration,etc.
	public Storage()
	{
		stackMenu << mainmenufilename
	} /// end of constructor


	// retrieve pointer to array of menus
	public getStorage()
	{
		return stackMenu;
	} // end of getCommands

	// test harness for this class
	public static void main(String[] args)
	{	
		Storage s = new Storage()	// started with main.txt on stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"
		println "--------------------------\n"

		s.leftShift("bill.txt")		// added bill to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"
		println "--------------------------\n"

		s.leftShift("./resources/eve")		// added fred to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		s.leftShift("./resources/fred.txt")		// added fred to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		s.leftShift("./resources/jim")		// added jim to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		s.leftShift("max")					// added max to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		s.leftShift("max.txt")					// added max to stack again, should not duplicate
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		println "starting F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred
		println "--------------------------\n"

		println "         F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred
		println "--------------------------\n"
		println "         F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred
		println "--------------------------\n"
		println "         F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred


		println "===================================="


		println "starting to pop() --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		s.pop()				// popped fred
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"

		s.pop()				// popped bill
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"

		s.pop()				// should not erase main.txt (the first stack entry)
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		println "stack at ${s.get()}\n\nnow add github"
		
		s << "github";
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"

		println "\n now add ubuntu.txt"
		s << "ubuntu.txt";
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"

		println "\n now s.pop()"
		def x1 = s.pop()				// popped bill
		println "\nx1 now "+x1;
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"

		println "\ns.pop() again"
		x1 = s.pop();			// popped bill
		println "\nx1 now "+x1;
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
	}

	// non-distructive retrieve prior menu file name
	public getPriorMenu()
	{	
		def value = null
		if (stackMenu.size()>1)
		{
			value = stackMenu[stackMenu.size()-2]
		}
		else
		{
			value = stackMenu[0]
		} // end of if
		return value
	} // end of getPriorMenu


	// non-distructive retrieve prior menu file name
	public getCurrentMenu()
	{	
		def value = null
		if (stackMenu.size()>0)
		{
			value = stackMenu[stackMenu.size()-1]
		}
		else
		{
			value = stackMenu[0]
		} // end of if
		return value
	} // end of getCurrentMenu


	// the << overload operator
	synchronized void leftShift(value)
	{
		if(audit) say "stackMenu.size() before push is ${stackMenu.size()}"
		def k = value.lastIndexOf("/");
		
		def va = (k>-1) ? value.substring(k+1) : value;
		k = va.lastIndexOf(".");
		va = (k>-1) ? va.substring(0,k) : va;
		if(audit) say "leftShift($value) becomes <${va}>"
		
		if (audit)
		{
			say "stackMenu[ ${stackMenu.size() - 1} ]="+stackMenu[stackMenu.size()-1]+" when trying to add "+va;
		} // end of if
		
		// don't add same menu twice
		if (stackMenu[stackMenu.size()-1] != va)
		{
			stackMenu.push(va);
		} // end of if
		else
		{
				if(audit) say "won't push duplicate <$va>"
		} // end of else
		
		
		if(audit) say "stackMenu.size() after push is ${stackMenu.size()}    \nstorage.pushed: $value at ${stackMenu.size()-1}"
		notifyAll()
	} // end of left...

	// 
	synchronized Object pop()
	{
		while (!stackMenu)
		{
			try{ wait() }
			catch(InterruptedException e) {}
		} // end of while

		def value = null
		if(audit) say "will storage.pop: stackMenu.size()= ${stackMenu.size()} but -1 is ${stackMenu.size()-1}"

		if (stackMenu.size()>1)
		{
			value = stackMenu.pop()
		    
		}
		else
		{
			value = stackMenu[0]
		} // end of if

		if(audit) say "storage.popped: $value at ${stackMenu.size()-1}"
		return value
	} // end of method

	synchronized Object get()
	{
		while (!stackMenu)
		{
			try{ wait() }
			catch(InterruptedException e) {}
		} // end of while

		def value = null
		if (stackMenu.size()>0)
		{
			value = stackMenu[stackMenu.size()-1]
		} // end of if
		if(audit) say "storage.get: $value at ${stackMenu.size()-1} and stackMenu.size() is ${stackMenu.size()}"
		return value
	} // end of method

} // end of class