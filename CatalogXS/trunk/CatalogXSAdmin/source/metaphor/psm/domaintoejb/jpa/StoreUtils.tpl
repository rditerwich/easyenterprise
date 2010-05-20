package metaphor.psm.domaintoejb.jpa

import metaphor.psm.java.IJavaCollection
import metaphor.psm.java.IJavaMap
import metaphor.psm.java.IJavaTypedElement
import metaphor.psm.java.IJavaWrapperType
import metaphor.psm.java.JavaBeanUtil
import metaphor.psm.jpa.Get
import metaphor.psm.jpa.IAbstractClass
import metaphor.psm.jpa.IJpaProperty
import metaphor.psm.jpa.IPrimaryKeyClass
import metaphor.psm.jpa.JpaUtil
import metaphor.psm.jpa.Set
import metaphor.tpl.lib.Exists
import metaphor.tpl.lib.Lcf
import metaphor.tpl.lib.Type
import metaphor.tpl.lib.Ucf
import metaphor.transformation.ModelError
import metaphor.util.ArrayUtil

[template PatchRemoveValue(IJpaProperty property, String objectExpression, String existingValueExpression) extends RemoveValue]
    [let IJavaWrapperType wrapper is property.wrapperType]
        if ([objectExpression] != null && [objectExpression].[Get(property)]() != null) {
            [let IJavaMap map is wrapper]
                [let IAbstractClass class is property.javaBean]
                    [objectExpression].[Get(property)]().keySet().remove([GetKey(class, existingValueExpression)])
                [/let]
            [else]
                [objectExpression].[Get(property)]().remove([existingValueExpression])
            [/let];
        }
    [else]
        [objectExpression].[Set(property)](null)
    [/let]
[/template]

