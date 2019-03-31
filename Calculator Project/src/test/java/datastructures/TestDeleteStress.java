package datastructures;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;

/**
 * This file should contain any tests that check and make sure your
 * delete method is efficient.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteStress extends TestDoubleLinkedList {
	@Test(timeout = SECOND) 
    public void testDeleteNearMidEfficient() {
    	int cap = 5000;
    	int c = cap / 2;
    	IList<Integer> list = makeIntList(cap);
    	for (int i = 0; i < c; i++) {
    		int data = list.get(c);
    		assertEquals(data, list.delete(c));
    	}
    }
    
    @Test(timeout = SECOND) 
    public void deleteNearEndEfficient() {
    	int cap = 99999;
    	IList<Integer> list = makeIntList(cap);
    	for (int i = 0; i < cap; i++) {
    		int c = list.size() - 1;
    		int data = list.get(c);
    		assertEquals(data, list.delete(c));
    	}
    	assertEquals(0, list.size());
    }
    
    @Test(timeout = 2 * SECOND) 
    public void deleteNearFrontEfficient() {
    	int cap = 999999;
    	IList<Integer> list = makeIntList(cap);
    	for (int i = 0; i < cap - 1; i++) {
    		int data = list.get(1);
    		assertEquals(data, list.delete(1));
    	}
    	assertEquals(1, list.size());
    }
    
    @Test(timeout = 5 * SECOND)
    public void deleteComboEfficient() {
    	int cap = 99999;
    	IList<Integer> list = makeIntList(cap);
    	for (int i = 0; i < cap / 10; i++) {
    		int data = list.get(0);
    		assertEquals(data, list.delete(0));
    		
    		int size = list.size() - 1;
    		data = list.get(size);
    		assertEquals(data, list.delete(size));
    		
    		size = list.size() - 1;
    		
    		data = list.get(size / 2);
    		assertEquals(data, list.delete(size / 2));
    	}
    }
    
    @Test(timeout = 5 * SECOND)
    public void testDeleteAndInsertMid() {
        int cap = 100000;
        int duration = 1000;
        IList<Integer> list = makeIntList(cap);
        int oValue = list.get(cap / 2);
        while (duration > 0) {
            list.insert(cap / 2, list.delete(cap / 2));
            assertEquals(oValue, list.get(cap / 2));
            duration--;
        }
        assertEquals(cap, list.size());
    }
    
    @Test(timeout = 5 * SECOND)
    public void testDeleteBigElements() {
        int cap = 15800;
        int target = cap / 2;
        IList<IList<Integer>> list = new DoubleLinkedList<>();
        IList<Integer> subList = makeIntList(cap);
        for (int i = 0; i < cap; i++) {
            list.add(subList);
        }
        for (int i = 0; i < cap - 1; i++) {
            assertEquals(target, list.get(target).delete(target));
            list.get(target).insert(target, target);
            if (i % 2 == 0) {
                list.remove();
            }
        }
        
        assertEquals(target, list.size());
    }
}
