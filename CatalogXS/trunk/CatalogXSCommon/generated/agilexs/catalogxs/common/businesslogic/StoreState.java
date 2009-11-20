package agilexs.catalogxs.common.businesslogic;

import java.lang.IllegalStateException;
import java.lang.Object;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class StoreState {
    private Map<Object, ProcessedStoreView> processedPropertiesPerObject = new HashMap<Object, ProcessedStoreView>();
    private Map<Object, Object> newToTargetMap = new IdentityHashMap<Object, Object>();
    private boolean requiresSecondRun = false;
    private boolean isSecondRun = false;

    public StoreState() {
    }

    public ProcessedStoreView getOrCreate(Object object){
        ProcessedStoreView processedProperties = processedPropertiesPerObject.get(object);
        if (processedProperties == null) {
            processedProperties = new ProcessedStoreView();
            processedPropertiesPerObject.put(object, processedProperties);
        }
        return processedProperties;
    }

    public boolean requiresSecondRun() {
        return requiresSecondRun;
    }

    public void setRequiresSecondRun() {
        requiresSecondRun = true;
    }

    public boolean isSecondRun() {
        return isSecondRun;
    }

    public void setSecondRun() {
        isSecondRun = true;
    }

    public void addToNewToTargetMap(Object newObject, Object targetObject) {
        // We are using an IdentityHashMap to store the objects. We want to compare references because we cannot
        // use the equals of newObject, its id fields could be empty (technical key situation) which could
        // lead to equal == true for 2 different objects of the same type.
        if (!newToTargetMap.containsKey(newObject)) { 
            newToTargetMap.put(newObject, targetObject);
        } else {
            throw new IllegalStateException("newToTargetMap already contains " + newObject);
        }
    }

    public Object getTargetFromNewToTargetMap(Object newObject) {
        return newToTargetMap.get(newObject);
    }

    public void resetProcessedProperties() {
        processedPropertiesPerObject = new HashMap<Object, ProcessedStoreView>();
    }
}