
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import org.apache.bookkeeper.metastore.Value;

@RunWith(MockitoJUnitRunner.class)
public class ValueTest {
	
	private Value value;

@Before
public void setup(){
	value = new Value();
}
	
@Test
public void test(){
	String[] fields = new String[] {"firstField", "secondField", "otherFirstField", "otherSecondField"};
	byte[][] values = new byte[][] {"bytesFirstField".getBytes(), "bytesSecondField".getBytes(), null, "otherBytesSecondField".getBytes()};
	value.setField(fields[0], values[0]);
	Value val = new Value();
	for(int i=1; i<fields.length; i++)
		val.setField(fields[i], values[i]);
	value.merge(val);
	HashMap<String, byte[]> hash = new HashMap<>();
	hash.put(fields[0], values[0]);
	hash.put(fields[1], values[1]);
	hash.put(fields[3], values[3]);
	
	Assert.assertEquals(value.getFields(), hash.keySet());
}
}
