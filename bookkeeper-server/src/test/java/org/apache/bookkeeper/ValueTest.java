package org.apache.bookkeeper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

//import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.bookkeeper.metastore.Value;

@RunWith(MockitoJUnitRunner.class)
public class ValueTest {
	
	private final String[] fields = new String[] {"firstField", "secondField", "nullValueField", null, "anotherField"};
	private final String[] values = new String[] {"bytesFirstField", "bytesSecondField", null, "nullFieldValue", "anotherBytesField"};
	
	
	@InjectMocks
	Value value = new Value();
	
	@Mock
	Map<String, byte[]> mockedField;

	@Before
	public void setup(){

		Mockito.when(mockedField.entrySet()).thenAnswer((Answer<Set<Entry<String, byte[]>>>) invocation -> {
			LinkedHashSet<Entry<String, byte[]>> set = new LinkedHashSet<>();
			byte[] temp;
			for(int i=0; i<fields.length; i++) {
				if (values[i] != null)
					temp = values[i].getBytes();
				else
					temp = null;
				set.add(new AbstractMap.SimpleEntry<>(fields[i], temp));
			}
			return set;
		});
		Mockito.lenient().when(mockedField.size()).thenReturn(fields.length);
	}
	
	@Test
	public void testMerge(){
		boolean condition = true;
		Value val = new Value();
		for(int i=0; i<fields.length; i++) {
			if (values[i] != null)
				val.setField(fields[i], values[i].getBytes());
			else
				val.setField(fields[i], null);
		}
		
		value.merge(val);
		
		for(int i=0; i<fields.length; i++) {
			try {
				if (values[i]!=null)
					Mockito.verify(mockedField).put(fields[i], values[i].getBytes());
				else
					Mockito.verify(mockedField).remove(fields[i]);
			} catch(Throwable e) {
				condition = false;
				break;
			}
		}
		Assert.assertTrue(condition);
	}
	
	@Test
	public void testToString() {
		String str = "[('firstField'=bytesFirstField)('secondField'=bytesSecondField)('nullValueField'=NONE)('NULL'=nullFieldValue)('anotherField'=anotherBytesField)]";
		Assert.assertEquals(value.toString(), str);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsNotSameInstance() {
		String obj = "wrongType";
		Assert.assertFalse(value.equals(obj));
	}
	
	@Test
	public void testEqualsNotSameSize() {
		boolean cond1 = value.equals(null);
		Value val = new Value();
		val.setField("field", "value".getBytes());
		
		Mockito.when(mockedField.size()).thenReturn(2);
		
		boolean cond2 = value.equals(val);
		Assert.assertTrue(!cond1 && !cond2);
	}
	
	@Ignore
	@Test
	public void testEqualsNotSameEntries1() {
		Value val = new Value();
		for(int i=0; i<fields.length-1; i++) {
			if (values[i] != null)
				val.setField(fields[i], values[i].getBytes());
			else
				val.setField(fields[i], null);		
			}
		val.setField("differentField", "differentBytes".getBytes());
		Assert.assertFalse(value.equals(val));
	}
	
	@Ignore
	@Test
	public void testEqualsNotSameEntries2() {
		Value val1 = new Value();
		Value val2 = new Value();
		val1.setField("field", "value".getBytes());
		val2.setField("differentField", "differentValue".getBytes());
		Assert.assertFalse(val1.equals(val2));
	}
	
	@Test
	public void testEqualsNotSameEntries3() {
		Value val1 = new Value();
		Value val2 = new Value();
		val1.setField("sameField", "value".getBytes());
		val2.setField("sameField", "differentValue".getBytes());
		Assert.assertFalse(val1.equals(val2));
	}
	
	@Ignore
	@Test
	public void testEqualsTrue1() {
		Value val = new Value();
		for(int i=0; i<fields.length; i++) {
			if (values[i] != null)
				val.setField(fields[i], values[i].getBytes());
			else
				val.setField(fields[i], null);
		}
		Assert.assertTrue(value.equals(val));
	}
	
	@Test
	public void testEqualsTrue2() {
		Value val1 = new Value();
		Value val2 = new Value();
		val1.setField("sameField", "sameValue".getBytes());
		val2.setField("sameField", "sameValue".getBytes());
		Assert.assertTrue(val1.equals(val2));
	}
	
	@Test
	public void testProjectTotal() {
		//ALL_FIELDS is initially set to null, we can exploit this to cover the first condition
		Value val1 = new Value();
		val1.setField("firstField", "firstValue".getBytes());
		val1.setField("secondField", "secondValue".getBytes());
		Value val2 = val1.project(null);
		Assert.assertEquals(val1.getFields().size(), val2.getFields().size());
		for (String entry : val1.getFields()){
			byte[] tmp = val2.getField(entry);
			Assert.assertEquals(tmp, val1.getField(entry));
		}
	}
	
	@Test
	public void testProjectPartial() {
		Value val1 = new Value();
		val1.setField("firstField", "firstValue".getBytes());
		val1.setField("secondField", "secondValue".getBytes());
		val1.setField("thirdField", "thirdValue".getBytes());
		val1.setField("lastField", "lastValue".getBytes());
		Set<String> set = new LinkedHashSet<>();
		set.add("secondField");
		set.add("lastField");
		Value val2 = val1.project(set);
		boolean cond1 = val2.getFields().size()==2;
		boolean cond2 = val2.getFields().contains("secondField");
		boolean cond3 = val2.getFields().contains("lastField");
		Assert.assertTrue(cond1 && cond2 && cond3);
	}
	
}