package datastructures;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

/**
 * This class should contain all the tests you implement to verify that
 * your 'delete' method behaves as specified.
 *
 * This test _extends_ your TestDoubleLinkedList class. This means that when
 * you run this test, not only will your tests run, all of the ones in
 * TestDoubleLinkedList will also run.
 *
 * This also means that you can use any helper methods defined within
 * TestDoubleLinkedList here. In particular, you may find using the
 * 'assertListMatches' and 'makeBasicList' helper methods to be useful.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteFunctionality extends TestDoubleLinkedList {
    
    @Test(timeout=SECOND)
    public void testBasicDelete() {
        IList<String> list = this.makeBasicList();
        list.delete(0);
        
        assertListMatches(new String[] {"b", "c"}, list);
        
        list.delete(1);
        assertListMatches(new String[] {"b"}, list);
        
        list.delete(0);
        assertEquals(0, list.size());
    }
    
    @Test(timeout=SECOND)
    public void testDeleteInvalidIndex() {
    	IList<Integer> list = new DoubleLinkedList<>();
    	list.add(1);
    	list.add(2);
    	list.add(3);
    	try {
    		list.delete(3);
    		fail("Expected IndexOutOfBoundsException");
    	} catch (IndexOutOfBoundsException ex) {
    	    // Do nothing
    	}
    	list.add(4);
    	try {
    		list.delete(3);
    	} catch (IndexOutOfBoundsException ex) {
    		fail("Throw exception when none needed");
    	}
    	
    	try {
    		list.delete(-1);
    		fail("Expected IndexOutOfBoundsException");
    	} catch (IndexOutOfBoundsException ex) {
    	    // Do nothing
    	}
    	
    	try {
    		list.delete(2147483647 + 1);
    		fail("Expected IndexOutOfBoundsException");
    	} catch (IndexOutOfBoundsException ex) {
    	    //DO NOTHING, IT IS OKAY
    	}
    }
    
    @Test(timeout = SECOND)
    public void testRemoveAndDelete() {
    	int cap = 999999;
    	IList<Integer> list = makeIntList(cap);

    	for (int i = 0; i < (cap / 2); i++) {
    		list.delete(0);
    		list.remove();
    	}
    	list.remove();
    	assertEquals(0, list.size());
    }
    
    @Test(timeout = SECOND)
    public void testInsertAndDelete() {
    	IList<Integer> list;
    	int cap = 3;
    	list = new DoubleLinkedList<>();
    	list.insert(0, 666);
    	assertEquals(666, list.delete(0));
    	list.insert(0, null);
    	list.insert(1, 2);
    	assertEquals(2, list.delete(1));
    	list = makeIntList(cap);
    	for (int i = 0; i < list.size(); i++) {
    		list.insert(i, i);
    		list.delete(i);
    		assertListMatches(new Integer[] {0, 1, 2}, list);
    	}
    	list.delete(2);
    	assertListMatches(new Integer[] {0, 1}, list);
    	list.delete(0);
    	assertListMatches(new Integer[] {1}, list);
    	
    	list.remove();
    	list.insert(0, null);
    	assertEquals(null, list.delete(0));
    }
    
    @Test(timeout = SECOND)
    public void testIteratorAndDelete() {
    	int cap = 1000000;
    	IList<Integer> list = makeIntList(cap);
    	Iterator<Integer> iter = list.iterator();
    	while (iter.hasNext()) {
    		Integer data = iter.next();
    		assertEquals(data, list.delete(list.indexOf(data)));
    	}
    	assertEquals(0, list.size());
    }
    
    @Test(timeout = SECOND)
    public void testDeleteNull() {
        IList<String> list = new DoubleLinkedList<>();
        
        list.add("a");
        list.add(null);
        list.add(null);
        list.add("a");
        
        list.delete(list.indexOf(null));
        assertListMatches(new String[] {"a", null, "a"}, list);
    }
    
    @Test(timeout = SECOND)
    public void testDeleteEmptyList() {
        IList<String> list = new DoubleLinkedList<>();
        for (int i = 0; i < 100; i++) {
            try {
                list.delete(list.size() - 1);
                list.delete(0);
                fail("Expected IndexOutOfBoundsException");
            } catch(IndexOutOfBoundsException e) {
                //DO NOTHING, this is okay
            }
        }
    }
    
    @Test(timeout = SECOND)
    public void testSetAndDelete() {
        int cap = 10000;
        IList<Integer> list = makeIntList(cap);
        
        for (int i = 0; i < cap; i++) {
            list.set(i, i + 100);
        }
        
        for (int i = 0; i < cap; i++) {
            if (list.get(0) == i + 100) {
                list.delete(0);
            }
        }
        
        assertEquals(0, list.size());
    }
    
    @Test(timeout = SECOND)
    public void testDeleteOdds() {
        IList<Integer> list = makeIntList(100);
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 != 0) {
                list.delete(i);
                i = i - 1;
            }
        }
        
        for (int j = 0; j < list.size(); j++) {
            assertTrue(list.get(j) % 2 == 0);
        }
    }
}
