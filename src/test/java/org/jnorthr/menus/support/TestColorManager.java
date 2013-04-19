package org.jnorthr.menus.support;

import static org.hamcrest.CoreMatchers.*; // let's me use 'assertThat'
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.*;
import org.jnorthr.menus.support.ColorManager;
import java.util.logging.Logger;
import java.util.logging.*;

public class TestColorManager {
	private final static Logger LOGGER = Logger.getLogger("TestColorManager");

	public TestColorManager() throws java.io.IOException
	{
		Handler ch = new FileHandler("TestColorManager.log");
		LOGGER.getLogger("TestColorManager").addHandler(ch); 
	}
	
	@Before 
	public void setUp() 
	{ 
		LOGGER.info("... started TestColorManager"); 
	}

    @Test
    public void test1()
    {
		String c = "#336699;Hi Kids !";
        ColorManager b = new ColorManager(c);
        assertThat(b, notNullValue() );

		String d = (String)b.getText();
        assertThat(d, containsString("#336699;Hi Kids !") );
        assertEquals(b.hasCode, true );

		d = (String)b.getItem();
        assertThat(d, containsString("Hi Kids !") );


		d = (String)b.getCode();
        assertThat(d, containsString("#336699") );

		d = (String)b.getHexCode();
        assertThat(d, containsString("336699") );

		int e = (int)b.getColorCode();
        assertEquals(e, 3368601 );

		e = (int)b.hasSemi(c);
        assertEquals(e, 7 );
		
		boolean ok = (boolean)b.getWord("#c00080;ok");
        assertEquals(ok, true );

		ok = (boolean)b.getWord("DarkRed;ok");
        assertEquals(ok, true );

		LOGGER.info("... test1 ok - not null and opaque=false"); 
    } // end of method

    @Test
    public void test2()
    {
		String c = "0x336699;Declare normal color signature 0x336699; with semicolon.";
        ColorManager b = new ColorManager(c);
	    assertThat(b, notNullValue() );
	    LOGGER.info("... test2 ok - not null"); 
	} // end of method

    @Test
    public void test3()
    {
		String c = "0x369;Declare normal color signature 0x369; with semicolon.";
        ColorManager b = new ColorManager(c);
  	    assertThat(b, notNullValue() );
	    //assertThat(b.getThickness(), is(3) );
	    //assertThat(b.getGap(), is(7) );
	    LOGGER.info("... test3 ok - "); 
	} // end of method

    @Test
    public void test4()
    {
		String c = "#336699;Declare normal color signature #336699; with semicolon.";
        ColorManager b = new ColorManager(c);
  	    assertThat(b, notNullValue() );
	    LOGGER.info("... test4 ok - "); 
	} // end of method

 
} // end of class