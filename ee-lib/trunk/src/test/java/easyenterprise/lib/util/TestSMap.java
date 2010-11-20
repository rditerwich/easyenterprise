package easyenterprise.lib.util;

import org.junit.Assert;
import org.junit.Test;

public class TestSMap {

	@Test
	public void testEmpty() {
		SMap<String, String> map = SMap.empty();
		Assert.assertEquals(true, map.isEmpty());
		Assert.assertEquals(true, map.getKeys().isEmpty());
		Assert.assertEquals(true, map.getValues().isEmpty());
		Assert.assertNull(map.getKey());
		Assert.assertNull(map.getValue());
		Assert.assertEquals(Empty.class, map.getClass());
	}
		
	@Test	
	public void testNoKeySingleValue() {
		SMap<String, String> map = SMap.empty();
		map = map.add("some value");
		Assert.assertEquals(false, map.isEmpty());
		Assert.assertEquals(false, map.getKeys().isEmpty());
		Assert.assertEquals(false, map.getValues().isEmpty());
		Assert.assertNull(map.getKey());
		Assert.assertEquals("some value", map.getValue());
		Assert.assertEquals(NoKeySingleValue.class, map.getClass());
	}
	
	@Test
	public void testNoKeyMultiValue() {
		SMap<String, String> map = SMap.empty();
		map = map.add("some value");
		map = map.add("another value");
		Assert.assertEquals(false, map.isEmpty());
		Assert.assertEquals(false, map.getKeys().isEmpty());
		Assert.assertEquals(false, map.getValues().isEmpty());
		Assert.assertNull(map.getKey());
		Assert.assertEquals("some value", map.getValue());
		Assert.assertEquals("some value", map.getValues().get(0));
		Assert.assertEquals("another value", map.getValues().get(1));
		Assert.assertEquals(NoKeyMultiValue.class, map.getClass());
	}
}
