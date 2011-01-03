package easyenterprise.lib.util;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public final class SMapSingleKeyMultiValue_CustomFieldSerializer {
  public static void deserialize(SerializationStreamReader s, SMapSingleKeyMultiValue<Object, Object> map) throws SerializationException {
  	map.key = s.readObject();
    map.values = new Object[s.readInt()];
    for (int i=0; i<map.values.length; i++)
    	map.values[i] = s.readObject(); 
	}
  public static void serialize(SerializationStreamWriter s, SMapSingleKeyMultiValue<Object, Object> map) throws SerializationException {
  	s.writeObject(map.key);
  	s.writeInt(map.values.length);
  	for (int i=0; i<map.values.length; i++)
  		s.writeObject(map.values[i]);
  }
}