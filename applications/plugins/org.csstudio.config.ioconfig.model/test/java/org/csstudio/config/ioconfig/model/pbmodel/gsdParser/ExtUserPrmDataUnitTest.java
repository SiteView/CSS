package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 20.07.2011
 */
public class ExtUserPrmDataUnitTest {
    
    private GSDFileDBO _gsdFileDBO;
    
    @Test
    public void defaults() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        assertTrue(out.getDefault()==0);
        out.setDefault("0");
        assertTrue(out.getDefault()==0);
        out.setDefault("-100000000");
        assertTrue(out.getDefault()==-100000000);
        out.setDefault("100000000");
        assertTrue(out.getDefault()==100000000);
        
        
        out.setDefault("0xA");
        assertFalse(out.getDefault()==10);
        assertTrue(out.getDefault()==0);
        out.setDefault("ten");
        assertFalse(out.getDefault()==10);
        assertTrue(out.getDefault()==0);
    }
    
    @Test
    public void maxBit() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("0");
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("-100000000");
        assertTrue(out.getMaxBit()==-100000000);
        out.setMaxBit("100000000");
        assertTrue(out.getMaxBit()==100000000);
        
        
        out.setMaxBit("0xA");
        assertFalse(out.getMaxBit()==10);
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("ten");
        assertFalse(out.getMaxBit()==10);
        assertTrue(out.getMaxBit()==0);
        
    }
    
    
    @Test
    public void maxValue() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        
        out.setValueRange("-100", "0");
        assertEquals(-100, out.getMinValue());
        assertEquals(0, out.getMaxValue());
        
        out.setValueRange("-200000000", "-100000000");
        assertEquals(-200000000, out.getMinValue());
        assertEquals(-100000000, out.getMaxValue());
        
        out.setValueRange("200000000", "100000000");
        assertEquals(100000000, out.getMinValue());
        assertEquals(200000000, out.getMaxValue());
        
        out.setValueRange("0xA", "0xA0");
        assertEquals(10, out.getMinValue());
        assertEquals(160, out.getMaxValue());
        
    }
    
    @Test
    public void minBit() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        assertTrue(out.getMinBit()==0);
        assertTrue(out.getMinBit()==0);
        out.setMinBit("0");
        assertTrue(out.getMinBit()==0);
        out.setMinBit("-100000000");
        assertTrue(out.getMinBit()==-100000000);
        out.setMinBit("100000000");
        assertTrue(out.getMinBit()==100000000);
        
        
        out.setMinBit("0xA");
        assertFalse(out.getMinBit()==10);
        assertTrue(out.getMinBit()==0);
        out.setMinBit("ten");
        assertFalse(out.getMinBit()==10);
        assertTrue(out.getMinBit()==0);
    }
    
    @Before
    public void setUp() throws Exception {
        _gsdFileDBO = new GSDFileDBO("JUnitTest", "#Profibus_DP\nVendor_Name            = JUnitTest");
    }
    
    @Test
    public void text() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "desc");
        assertEquals(out.getText(), "desc");
        out.setText("");
        assertEquals(out.getText(), "");
        out.setText("^1234567890ߴqwertzuiop�+asdfghjkl��#yxcvbnm,.-QAY\\\"");
        assertEquals(out.getText(), "^1234567890ߴqwertzuiop�+asdfghjkl��#yxcvbnm,.-QAY\\\"");
        
    }
}
