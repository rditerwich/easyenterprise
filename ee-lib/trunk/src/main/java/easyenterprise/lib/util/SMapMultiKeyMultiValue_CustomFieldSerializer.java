package easyenterprise.lib.util;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;


public final class SMapMultiKeyMultiValue_CustomFieldSerializer {
  public static void deserialize(SerializationStreamReader s, SMapMultiKeyMultiValue<Object, Object> map) throws SerializationException {
  	map.keys = new Object[s.readInt()];
    for (int i=0; i<map.keys.length; i++)
    	map.keys[i] = s.readObject();
    map.values = new Object[s.readInt()][];
    for (int i=0; i<map.values.length; i++) {
    	Object[] values = map.values[i] = new Object[s.readInt()];
    	for (int j=0; j < values.length; j++)
    		values[j] = s.readObject();
    }
	}
  public static void serialize(SerializationStreamWriter s, SMapMultiKeyMultiValue<Object, Object> map) throws SerializationException {
  	s.writeInt(map.keys.length);
  	for (int i=0; i<map.keys.length; i++)
  		s.writeObject(map.keys[i]);
  	s.writeInt(map.values.length);
  	for (int i=0; i<map.values.length; i++) {
  		Object[] values = map.values[i];
			s.writeInt(values.length);
			for (int j=0; j<values.length; j++) 
				s.writeObject(values[j]);
  	}
  }
}