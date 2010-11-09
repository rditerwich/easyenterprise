/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The <code>AttributeOverride</code> annotation is used to 
 * override the mapping of a {@link Basic} (whether explicit or 
 * default) property or field or Id property or field.
 *
 * <p> The <code>AttributeOverride</code> annotation may be 
 * applied to an entity that extends a mapped superclass or to 
 * an embedded field or property to override a basic mapping 
 * defined by the mapped superclass or embeddable class. If the 
 * <code>AttributeOverride</code> annotation is not specified, 
 * the column is mapped the same as in the original mapping.
 *
 * <pre>
 * <p> Example:
 *
 *   &#064;MappedSuperclass
 *   public class Employee {
 *       &#064;Id protected Integer id;
 *       &#064;Version protected Integer version;
 *       protected String address;
 *       public Integer getId() { ... }
 *       public void setId(Integer id) { ... }
 *       public String getAddress() { ... }
 *       public void setAddress(String address) { ... }
 *   }
 *
 *   &#064;Entity
 *   &#064;AttributeOverride(name="address", column=&#064;Column(name="ADDR"))
 *   public class PartTimeEmployee extends Employee {
 *       // address field mapping overridden to ADDR
 *       protected Float wage();
 *       public Float getHourlyWage() { ... }
 *       public void setHourlyWage(Float wage) { ... }
 *   }
 * </pre>
 *
 * @see Embedded
 * @see Embeddable
 * @see MappedSuperclass
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface AttributeOverride {

    /**
     * (Required) The name of the property whose mapping is being 
     * overridden if property-based access is being used, or the 
     * name of the field if field-based access is used.
     */
    String name();

    /**
     * (Required) The column that is being mapped to the persistent 
     * attribute. The mapping type will remain the same as is 
     * defined in the embeddable class or mapped superclass.
     */
    Column column();
}