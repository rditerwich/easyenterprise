package easyenterprise.lib.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

public class TestSMap {

	@Test
	public void testEmpty() {
		SMap<String, String> map = SMap.empty();
		Assert.assertEquals(true, map.isEmpty());
		Assert.assertEquals(true, map.getKeys().isEmpty());
		Assert.assertEquals(true, map.getAllFirst().isEmpty());
		Assert.assertNull(map.getFirstKey());
		Assert.assertNull(map.getFirst());
		Assert.assertEquals(SMapEmpty.class, map.getClass());
	}
		
	@Test	
	public void testNoKeySingleValue() {
		SMap<String, String> map = SMap.empty();
		map = map.add("some value");
		Assert.assertEquals(false, map.isEmpty());
		Assert.assertEquals(false, map.getKeys().isEmpty());
		Assert.assertEquals(false, map.getAllFirst().isEmpty());
		Assert.assertNull(map.getFirstKey());
		Assert.assertEquals("some value", map.getFirst());
		Assert.assertEquals(SMapNoKeySingleValue.class, map.getClass());
	}
	
	@Test
	public void testNoKeyMultiValue() {
		SMap<String, String> map = SMap.empty();
		map = map.add("some value");
		map = map.add("another value");
		Assert.assertEquals(false, map.isEmpty());
		Assert.assertEquals(false, map.getKeys().isEmpty());
		Assert.assertEquals(false, map.getAllFirst().isEmpty());
		Assert.assertNull(map.getFirstKey());
		Assert.assertEquals("some value", map.getFirst());
		Assert.assertEquals("some value", map.getAllFirst().get(0));
		Assert.assertEquals("another value", map.getAllFirst().get(1));
		Assert.assertEquals(SMapNoKeyMultiValue.class, map.getClass());
	}
	
	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		SMap<String, String> map = SMap.empty();
		Assert.assertEquals(map, serialize(map));
		
		map = SMap.empty();
		map = map.add("value1");
		Assert.assertEquals(map, serialize(map));
		
		map = SMap.empty();
		map = map.add("key1", "value1");
		Assert.assertEquals(map, serialize(map));
		
		map = SMap.empty();
		map = map.add("value1");
		map = map.add("value2");
		Assert.assertEquals(map, serialize(map));
		
		map = SMap.empty();
		map = map.add("key1", "value1");
		map = map.add("key1", "value2");
		Assert.assertEquals(map, serialize(map));
		
		map = SMap.empty();
		map = map.add("key1", "value1");
		map = map.add("key2", "value2");
		Assert.assertEquals(map, serialize(map));
	}

	@SuppressWarnings("unchecked")
	private static SMap<String, String> serialize(SMap<String, String> smap) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bytes);
		os.writeObject(smap);
		os.close();
		return (SMap<String, String>) new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
	}
}
