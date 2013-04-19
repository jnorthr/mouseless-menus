package org.jnorthr.samples;

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

public class MathOpTest {
    @Test
    public void testAdd(){
        MathOp a = new MathOp();
        assertEquals(4, a.add(3, 1));       }
 
    @Test
    public void testSub(){
        MathOp a = new MathOp();
        assertEquals(-4, a.sub(-3, 1));
    }
}