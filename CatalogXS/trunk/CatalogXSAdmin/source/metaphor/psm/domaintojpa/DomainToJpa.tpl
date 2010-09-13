package metaphor.psm.domaintojpa

import java.util.ArrayList
import java.util.List
import metaphor.core.IModelPackage
import metaphor.pim.domain.DomainUtil
import metaphor.pim.domain.IAttribute
import metaphor.pim.domain.IClass
import metaphor.pim.domain.IDomainElement
import metaphor.pim.domain.IEnumeration
import metaphor.pim.domain.ILiteral
import metaphor.pim.domain.IPrimitiveType
import metaphor.psm.domaintordbms.DomainToRdbmsUtil
import metaphor.psm.domaintordbms.IAttributeToColumn
import metaphor.psm.domaintordbms.IAttributeToColumns
import metaphor.psm.domaintordbms.IClassToPrimaryKey
import metaphor.psm.domaintordbms.IClassToTable
import metaphor.psm.java.IJavaCollection
import metaphor.psm.java.IJavaEnum
import metaphor.psm.java.IJavaList
import metaphor.psm.java.IJavaPackage
import metaphor.psm.java.IJavaSet
import metaphor.psm.java.IJavaTypedElement
import metaphor.psm.java.IJavaWrapperType
import metaphor.psm.jpa.CascadeType
import metaphor.psm.jpa.EntityRelationship
import metaphor.psm.jpa.FetchType
import metaphor.psm.jpa.GenerationType
import metaphor.psm.jpa.IAbstractClass
import metaphor.psm.jpa.IEntityClass
import metaphor.psm.jpa.IGeneratedValue
import metaphor.psm.jpa.IJoinColumn
import metaphor.psm.jpa.IJoinTable
import metaphor.psm.jpa.IJpaPackage
import metaphor.psm.jpa.IJpaProperty
import metaphor.psm.jpa.IMappedSuperClass
import metaphor.psm.jpa.IPersistenceUnit
import metaphor.psm.jpa.IPrimaryKeyClass
import metaphor.psm.jpa.ISequenceGenerator
import metaphor.psm.jpa.InheritanceStrategy
import metaphor.psm.rdbms.IColumn
import metaphor.psm.rdbms.IPrimaryKeyConstraint
import metaphor.psm.rdbms.RdbmsVendor
import metaphor.psm.rdbms.Type
import metaphor.tpl.lib.Exists
import metaphor.tpl.lib.TypeName
import metaphor.transformation.AssertNotNull
import metaphor.transformation.Container
import metaphor.transformation.Create
import metaphor.transformation.Id
import metaphor.transformation.Match
import metaphor.transformation.Parameter
import metaphor.transformation.Parameters
import metaphor.transformation.RequireTransformed
import metaphor.transformation.Source
import metaphor.transformation.Sources
import metaphor.transformation.Transform
import metaphor.transformation.TransformationUtil
import metaphor.collections.CollectionUtil
import metaphor.util.StringUtil
import metaphor.collections.IList
import mof.ICollection
import mof.IObject
import mof.ISequence

import package metaphor.psm.domaintordbms as d2r
import package metaphor.psm.rdbms as rdbms

method void PatchAttributeToProperty(IAttributeToProperty attrToColToProp, IAbstractClass jpaClass) extends AttributeToProperty
    attrToColToProp.name = attrToColToProp.source.name + "ToProperty"
    var IAttributeToColumns attrToCols is attrToColToProp.source
    var IAttribute attr is attrToCols.source

    let IJpaProperty prop is Match(IAttributeToProperty.Attributes.target, Container(jpaClass.properties))
        prop.name = UniqueId(attr.name, prop, jpaClass.properties, false)
                        if prop.name.equals("parents") || prop.name.equals("children")  
                        System.out.println("XXXX A2P prop: " + prop.name)
                        /if

        if attr.isDerived
            prop.isTransient = true
        /if
        
        prop.wrapperType = DetermineWrapperType(attr)
        let IClass referredClass is attr.type
            // Determine mapped entity.
            let IClassToEntity mapping is RequireTransformed(DomainToJpaUtil.findJpaClassMapping(referredClass, attrToColToProp)) 
                prop.type = mapping.entity
                prop.customType = null
                prop.relationship = DetermineRelationship(attr)
                prop.isOptional = DomainUtil.isOptional(attr)
                if prop.relationship == EntityRelationship.OneToMany || prop.relationship == EntityRelationship.ManyToMany
                    prop.fetchType = FetchType.Lazy
                /if
                if attr.isComposite
                    prop.cascade.setAll({ CascadeType CascadeType.All })
                else 
                    prop.cascade.setAll(CollectionUtil.list(CascadeType.Persist, CascadeType.Merge)) // XXX Is this a good default? Maybe we should not cascade anything here...
                /if
                
                // Join Columns and Join Tables
                let IAttribute oppositeAttribute is attr.opposite
                    // bi-directional
                    
                    let IAttributeToProperty oppositeAttributeMapping is RequireTransformed(DomainToJpaUtil.findJpaAttributeMapping(oppositeAttribute, attrToColToProp))
                        let IJpaProperty oppositeProperty is oppositeAttributeMapping.target
                        if prop.name.equals("parents") || prop.name.equals("children")  
                        if (oppositeProperty == null)
                        System.out.println("prop: " + prop.name + " opposite: aaaahhhhh! Mapping: " + oppositeAttributeMapping.name + " source: " + oppositeAttributeMapping.source)
                        else
                        System.out.println("prop: " + prop.name + " opposite: " + oppositeProperty.name + " Mapping: " + oppositeAttributeMapping.name + " source: " + oppositeAttributeMapping.source)
                        /if
                        System.out.println("relationship: " + prop.relationship + " attrsToCols.atc.size(): " + attrToCols.attributeToColumn.size())
                        /if
                        if DomainToJpaUtil.isMappedBy(prop, oppositeProperty, prop.relationship, attrToCols.attributeToColumn.size() > 0)
                            if attrToCols.joinTable != null
                                // JoinTable
                                Match(IAttributeToProperty.Attributes.attributeToJoinTable, Source(attrToCols.joinTable), Parameters(prop, oppositeAttributeMapping.source.attributeToColumn, attrToCols.attributeToColumn))
                            else
                                Match(IAttributeToProperty.Attributes.attributeToJoinColumn, Sources(attrToCols.attributeToColumn), Parameter(prop.joinColumns))
                            /if
                        else
                            prop.mappedBy = oppositeProperty
                        /if
                        /let
                    /let
                else
                    // uni-directional
                    if attrToCols.joinTable != null
                        // JoinTable
                        var IClassToTable classToTable is attrToCols.containingClassToTable.table != null ? attrToCols.containingClassToTable : RequireTransformed(DomainToRdbmsUtil.findRootClassToTable(attrToCols.containingClassToTable))
                        if classToTable.table != null
                            var IList<IAttributeToColumn> joinColumns is DomainToJpaUtil.selectJoinColumns(attrToCols.attributeToColumn, classToTable.table)
                            var IList<IAttributeToColumn> inverseJoinColumns is DomainToJpaUtil.selectInverseJoinColumns(attrToCols.attributeToColumn, classToTable.table)

                            Match(IAttributeToProperty.Attributes.attributeToJoinTable, Source(attrToCols.joinTable), Parameters(prop, joinColumns, inverseJoinColumns))
                        /if
                    else
                        Match(IAttributeToProperty.Attributes.attributeToJoinColumn, Sources(attrToCols.attributeToColumn), Parameter(prop.joinColumns))
                    /if
                /let
                
            /let
        else
            let IEnumeration enum is attr.type
                let IEnumToEnum e2e is DomainToJpaUtil.findJpaEnumMapping(enum, attrToColToProp)
                    prop.type = e2e.target
                    prop.customType = null
                    prop.isId = attr.isID
                    prop.isOptional = DomainUtil.isOptional(attr)
                    if !attr.isDerived
                        prop.column = attrToCols.attributeToColumn.iterator().next().column
                    /if
                /let
            elselet IPrimitiveType primitiveType 
                prop.type = null
                prop.customType = DomainToJpaUtil.convert(primitiveType)
                prop.isId = attr.isID
                prop.isOptional = DomainUtil.isOptional(attr)
                if !attr.isDerived
                    prop.column = attrToCols.attributeToColumn.iterator().next().column
                /if
            /let
        /let
    /let
/method

private method IJavaWrapperType DetermineWrapperType(IAttribute attr)
    if DomainUtil.isMany(attr)
        if attr.isUnique
            return attr.repository().create(IJavaSet.class)
        /if
        if attr.isOrdered
            return attr.repository().create(IJavaList.class)
        /if
        
        return attr.repository().create(IJavaCollection.class)
    /if 
    
    return null
/method

private method EntityRelationship DetermineRelationship(IAttribute attr)
    if DomainUtil.isMany(attr)
        let IAttribute opposite is attr.opposite
            if DomainUtil.isMany(opposite)
                return EntityRelationship.ManyToMany
            else
                return EntityRelationship.OneToMany
            /if
        else
            return EntityRelationship.ManyToMany
        /let
    else
        let IAttribute opposite is attr.opposite
            if DomainUtil.isMany(opposite)
                return EntityRelationship.ManyToOne
            else
                return EntityRelationship.OneToOne
            /if
        else
            return EntityRelationship.ManyToOne
        /let
    /if 
/method

private method <T extends IObject, U extends T> String UniqueId(String requestedId, U object, ICollection<T> objects, boolean uppercaseFirst)
    return TransformationUtil.uniqueId(StringUtil.splitWords(requestedId, "", uppercaseFirst, true, false, true), object, objects)
/method
