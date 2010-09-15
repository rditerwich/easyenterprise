package metaphor.psm.javaeeaspects

import metaphor.psm.domaintoejb.jpa.EntityManagerField
import metaphor.psm.domaintoejb.jpa.GetValues
import metaphor.psm.domaintoejb.jpa.RemoveValue
import metaphor.psm.ejb.ISessionBean
import metaphor.psm.java.IJavaCollection
import metaphor.psm.java.IJavaTypedElement
import metaphor.psm.java.JavaBeanUtil
import metaphor.psm.java.UseMetaProperty
import metaphor.psm.jpa.Get
import metaphor.psm.jpa.IAbstractClass
import metaphor.psm.jpa.IEntityClass
import metaphor.psm.jpa.IJpaProperty
import metaphor.psm.jpa.JpaUtil
import metaphor.psm.jpa.Set
import metaphor.tpl.lib.Exists
import metaphor.tpl.lib.Lcf
import metaphor.tpl.lib.Type
import metaphor.tpl.lib.Ucf

[template PatchRemoveProperty(ISessionBean bean, IAbstractClass entityClass, String expression) extends RemoveProperty]
    [var String oldExpression is "old" + Ucf(entityClass.name)]
    [EntityManagerField].remove([expression]);
    [for IJpaProperty referringProp in entityClass.referringObjects(IJavaTypedElement.Attributes.type) where referringProp.mappedBy == null && !Exists({IJpaProperty prop in JavaBeanUtil.attributeExtent(entityClass) where JpaUtil.getReferencedProperty(prop) == referringProp})]

        // remove [referringProp.javaBean.name].[referringProp.name] references
        [let IEntityClass referringPropType is referringProp.type]
            [let IJavaCollection col is referringProp.wrapperType]
                [Type("javax.persistence.Query")] [Lcf(referringProp.javaBean.name)][Ucf(referringProp.name)]Query = [EntityManagerField].createQuery("SELECT [Lcf(referringProp.javaBean.name.substring(0, 1))] FROM [Type(referringProp.javaBean)] [Lcf(referringProp.javaBean.name.substring(0, 1))] WHERE ?1 MEMBER OF [Lcf(referringProp.javaBean.name.substring(0, 1))].[referringProp.name]"); 
                [Lcf(referringProp.javaBean.name)][Ucf(referringProp.name)]Query.setParameter(1, [expression]); 
                for (Object object : [Lcf(referringProp.javaBean.name)][Ucf(referringProp.name)]Query.getResultList()) {
                    [if referringProp.isOptional]
                        (([Type(referringProp.javaBean)]) object).[Get(referringProp)]().remove([expression]);
                    [else]
                        [var String requestedPropertyViewName is Lcf(referringProp.javaBean.name) + "View"]
                        [let IEntityClass propertyEntityClass is referringProp.container()]
                            [var ISessionBean propertySessionBean is GetStoreBean(bean, propertyEntityClass)]
                            [StoreViewType(bean)] [requestedPropertyViewName] = requestedProperties.findView([UseMetaProperty(propertyEntityClass, referringProp)]);
                            [SessionBeanField(propertySessionBean)].store(([Type(referringProp.javaBean)]) object, ([Type(referringProp.javaBean)]) null, ([Type(referringProp.javaBean)]) object, [requestedPropertyViewName], storeState[if !JavaBeanUtil.subClasses(propertyEntityClass).isEmpty()], true[/if]);
                         [/let]
                    [/if]
                }
            [else]
                [Type("javax.persistence.Query")] [Lcf(referringProp.javaBean.name)][Ucf(referringProp.name)]Query = [EntityManagerField].createQuery("SELECT [Lcf(referringProp.javaBean.name.substring(0, 1))] FROM [Type(referringProp.javaBean)] [Lcf(referringProp.javaBean.name.substring(0, 1))] WHERE [Lcf(referringProp.javaBean.name.substring(0, 1))].[referringProp.name] = ?1");
                [Lcf(referringProp.javaBean.name)][Ucf(referringProp.name)]Query.setParameter(1, [expression]); 
                for (Object object : [Lcf(referringProp.javaBean.name)][Ucf(referringProp.name)]Query.getResultList()) {
                    [if referringProp.isOptional]
                        (([Type(referringProp.javaBean)]) object).[Set(referringProp)](null);
                    [else]
                        [var String requestedPropertyViewName is Lcf(referringProp.javaBean.name) + "View"]
                        [let IEntityClass propertyEntityClass is referringProp.container()]
                            [var ISessionBean propertySessionBean is GetStoreBean(bean, propertyEntityClass)]
                            [StoreViewType(bean)] [requestedPropertyViewName] = requestedProperties.findView([UseMetaProperty(propertyEntityClass, referringProp)]);
                            [SessionBeanField(propertySessionBean)].store(([Type(referringProp.javaBean)]) object, ([Type(referringProp.javaBean)]) null, ([Type(referringProp.javaBean)]) object, [requestedPropertyViewName], storeState[if !JavaBeanUtil.subClasses(propertyEntityClass).isEmpty()], true[/if]);
                         [/let]
                    [/if]
                }
            [/let]
        [/let]
    [/for]
    [for IJpaProperty property in JavaBeanUtil.attributeExtent(entityClass) where !property.isId && !property.isTransient]
        [let IEntityClass propType is property.type]
        [let IEntityClass propertyEntityClass is property.container()]
            if (requestedProperties.includedProperties().contains([UseMetaProperty(propertyEntityClass, property)])) {
                if (processedProperties.includedProperties().add([UseMetaProperty(propertyEntityClass, property)])) {
                    [let IJavaCollection col is property.wrapperType]
                        [var String targetName is entityClass.name.equals(property.name) ? "_target" + Ucf(property.name) : "target" + Ucf(property.name)]
                        [var String requestedPropertyViewName is property.name + "View"]
                        [var IJpaProperty referencedProperty is JpaUtil.getReferencedProperty(property)]
                        // Remove reference: [property.name]
                        [if referencedProperty != null]
                            for ([Type(propType)] [targetName] : [GetValues(property, col, oldExpression)]) {
                                if (requestedProperties.forceComposite([UseMetaProperty(propertyEntityClass, property)])) {
                                    // Remove as composite
                                    [var ISessionBean propertySessionBean is GetStoreBean(bean, propType)
                                     // Send targetName as 'old' object, this will delete the targetName object]
                                    [StoreViewType(bean)] [requestedPropertyViewName] = requestedProperties.findView([UseMetaProperty(propertyEntityClass, property)]);
                                    [SessionBeanField(propertySessionBean)].store(([Type(propType)])[targetName], ([Type(propType)]) null, ([Type(propType)])[targetName], [requestedPropertyViewName], storeState[if !JavaBeanUtil.subClasses(propType).isEmpty()], true[/if]);
                                } else {
                                    [RemoveValue(referencedProperty, targetName, expression)];
                                }
                            }
                        [/if]
                        [expression].[Get(property)]().clear();
                    [else]
                        [let IJpaProperty referencedProperty is JpaUtil.getReferencedProperty(property)]
                            if ([expression] != null && [expression + "." + Get(property) + "()"] != null) {
                                // Remove referring reference: [property.name]
                                [RemoveValue(referencedProperty, expression + "." + Get(property) + "()", expression)];
                            }
                        [/let]
                    [/let]
                }
            }
        [/let]
        [/let]
    [/for]
[/template]
