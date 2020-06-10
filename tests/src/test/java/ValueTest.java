
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
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
	
	private String[] fields = new String[] {"firstField", "secondField", "nullValueField", null, "anotherField"};
	private String[] values = new String[] {"bytesFirstField", "bytesSecondField", null, "nullFieldValue", "anotherBytesField"};
	
	
	@InjectMocks
	Value value = new Value();
	
	@Mock
	Map<String, byte[]> mockedField;

	@Before
	public void setup(){

		Mockito.when(mockedField.entrySet()).thenAnswer(new Answer<Set<Map.Entry<String, byte[]>>>(){
			@Override
			public Set<Entry<String, byte[]>> answer(InvocationOnMock invocation) throws Throwable {
				Set<Entry<String, byte[]>> set = new LinkedHashSet<Entry<String, byte[]>>();
				byte[] temp;
				for(int i=0; i<fields.length; i++) {
					if (values[i] != null)
						temp = values[i].getBytes();
					else
						temp = null;
					set.add(new AbstractMap.SimpleEntry<String, byte[]>(fields[i], temp));
				}
				return set;
			}
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
		String str = "[";
		for(int i=0; i<fields.length; i++) {
			str += "('" + (fields[i]==null ? "NULL" : fields[i]) + "'=" + (values[i]==null ? "NONE" : values[i]) + ")";
		}
		str += "]";
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
	
}