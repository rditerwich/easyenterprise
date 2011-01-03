package easyenterprise.lib.util;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public final class SMapNoKeyMultiValue_CustomFieldSerializer {
  public static void deserialize(SerializationStreamReader s, SMapNoKeyMultiValue<Object, Object> map) throws SerializationException {
    map.values = new Object[s.readInt()];
    for (int i=0; i<map.values.length; i++)
    	map.values[i] = s.readObject(); 
	}
  public static void serialize(SerializationStreamWriter s, SMapNoKeyMultiValue<Object, Object> map) throws SerializationException {
  	s.writeInt(map.values.length);
  	for (int i=0; i<map.values.length; i++)
  		s.writeObject(map.values[i]);
  }
}