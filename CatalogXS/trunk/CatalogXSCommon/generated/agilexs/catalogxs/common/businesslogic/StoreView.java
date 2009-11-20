package agilexs.catalogxs.common.businesslogic;

import java.lang.Boolean;
import java.lang.RuntimeException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import agilexs.catalogxs.common.persistence.JpaProperty;

public class StoreView {
    private Set<JpaProperty> properties;
    private Map<JpaProperty, StoreView> propertyViews;
    private Map<JpaProperty, StoreView> forceCompositeViews;
    private StoreView parent = null;

    public StoreView(JpaProperty... properties) {
        this.properties = new HashSet<JpaProperty>(properties.length);
        this.properties.addAll(Arrays.asList(properties));
    }

    public Set<JpaProperty> includedProperties() {
        return properties;
    }

    public void addView(JpaProperty property, StoreView view, Boolean forceComposite) {
        view.parent = this;
        if (propertyViews == null) {
            propertyViews = new HashMap<JpaProperty, StoreView>();
        }
        if (propertyViews.put(property, view) != null) {
            throw new RuntimeException("A view for property " + property + " already present");
        }
        if (forceComposite && forceCompositeViews == null) {
            forceCompositeViews = new HashMap<JpaProperty, StoreView>();
        }
        if (forceComposite && forceCompositeViews.put(property, view) != null) {
            throw new RuntimeException("A force composite view for property " + property + " already present");
        }
    }

    public StoreView findView(JpaProperty property) {
        if (propertyViews != null) {
            StoreView propertyView = propertyViews.get(property);
            if (propertyView != null) {
                return propertyView;
            }
        }

        // fallback to parent:
        if (parent != null) {
            return parent.findView(property);
        }

        return null;
    }

    public boolean forceComposite(JpaProperty property) {
        if (forceCompositeViews != null) {
            return forceCompositeViews.containsKey(property);
        }

        // fallback to parent:
        if (parent != null) {
            return parent.forceComposite(property);
        }

        return false;
    }
}