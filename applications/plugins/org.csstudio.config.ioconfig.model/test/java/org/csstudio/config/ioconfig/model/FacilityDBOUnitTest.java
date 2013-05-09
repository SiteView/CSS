package org.csstudio.config.ioconfig.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 02.08.2011
 */
public class FacilityDBOUnitTest {
    
    @BeforeClass
    public static void setUp() throws Exception {
        Repository.injectIRepository(new DummyRepository());
    }
    //    @After
    //    public void setDown() {
    //        Repository.injectIRepository(null);
    //    }
    
    @Test
    public void compareFacilitys() throws Exception {
        final DocumentDBO document = new DocumentDBO("subDoc","descDoc","keyDoc");
        final Set<DocumentDBO> docs = new HashSet<DocumentDBO>();
        docs.add(document);
        final FacilityDBO facility1 = createFacility(docs);
        final FacilityDBO facility2 = createFacility(docs);
        
        facility1.localSave();
        facility2.localSave();
        
        assertTrue(facility1.equals(facility1));
        assertTrue(facility2.equals(facility2));
        assertFalse(facility1.equals(facility2));
        assertFalse(facility2.equals(facility1));
        
        assertEquals(facility1, facility1);
        assertEquals(facility2, facility2);
        
        assertNotSame(facility1, facility2);
        assertNotSame(facility2, facility1);
        
        assertFalse(0==facility1.compareTo(facility2));
        assertFalse(0==facility2.compareTo(facility1));
        
    }
    @Test
    public void createFacility() throws PersistenceException{
        final DocumentDBO document = new DocumentDBO("subDoc","descDoc","keyDoc");
        final Set<DocumentDBO> docs = new HashSet<DocumentDBO>();
        docs.add(document);
        final FacilityDBO facility = createFacility(docs);
        assertEquals(0,facility.getId());
        assertEquals(null, facility.getParent());
        assertEquals("Creater", facility.getCreatedBy());
        assertEquals(Date.valueOf("2011-11-11"),facility.getCreatedOn());
        assertEquals("Updater", facility.getUpdatedBy());
        assertEquals(Date.valueOf("2012-12-12"), facility.getUpdatedOn());
        assertEquals("description line 1\r\ndescription line 2",facility.getDescription());
        assertEquals(docs,facility.getDocuments());
        assertEquals("FacNameTest",facility.getName());
        assertEquals((short)12, (short) facility.getSortIndex());
        assertEquals(11,facility.getVersion());
        
        facility.localSave();
        
        assertTrue(facility.getId()>0);
        assertEquals(null, facility.getParent());
        assertEquals("Creater", facility.getCreatedBy());
        assertEquals(Date.valueOf("2011-11-11"),facility.getCreatedOn());
        assertEquals("Updater", facility.getUpdatedBy());
        assertEquals(Date.valueOf("2012-12-12"), facility.getUpdatedOn());
        assertEquals("description line 1\r\ndescription line 2",facility.getDescription());
        assertEquals(docs,facility.getDocuments());
        assertEquals("FacNameTest",facility.getName());
        assertEquals((short)12, (short) facility.getSortIndex());
        assertEquals(11,facility.getVersion());
    }
    /**
     * @param docs
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    private FacilityDBO createFacility(@Nonnull final Set<DocumentDBO> docs) throws PersistenceException {
        final FacilityDBO facility = new FacilityDBO();
        facility.setCreatedBy("Creater");
        facility.setCreatedOn(Date.valueOf("2011-11-11"));
        facility.setUpdatedBy("Updater");
        facility.setUpdatedOn(Date.valueOf("2012-12-12"));
        facility.setDescription("description line 1\r\ndescription line 2");
        facility.setDocuments(docs);
        facility.setName("FacNameTest");
        facility.setSortIndexNonHibernate((short)12);
        facility.setVersion(11);
        return facility;
    }
    
}
//CHECKSTYLE:ON
