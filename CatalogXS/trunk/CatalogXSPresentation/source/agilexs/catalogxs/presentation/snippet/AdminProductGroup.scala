package agilexs.catalogxs.presentation.snippet

import javax.ejb.EJB;
import javax.naming.InitialContext;

import net.liftweb._ 
import http._ 
import SHtml._ 
import S._ 
 
import js._ 
import JsCmds._ 
 
import mapper._ 
 
import util._ 
import Helpers._

import scala.xml.{NodeSeq, Text} 

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Model.{setToWrapper,listToWrapper}
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.businesslogic.query._
import agilexs.catalogxs.businesslogic.CatalogBean
/**
<table>
  	  <th>
  	    <td><lift:loc locid="productgroup.name">Name</lift:loc></td>
  	    <td>&nbsp;</td>
  	  </th>
<!--
  <lift:AdminProductGroup.listChildren>
	<tr>
	  <td><productGroup:name/></td>
	  <td><productGroup:detail/></td>
	</tr>
  </lift:AdminProductGroup.listChildren>
    <lift:ProductAdmin.list>
  	  <tr>
  	    <td><product:articleNumber /></td>
  	    <td><product:synopsis /></td>
  	    <td><product:price /></td>
  	    <td><product:promotion /></td>
  	  </tr>
    </lift:ProductAdmin.list>
  </table>
  <div>Add product:</div>
    <product:newName/><product:buttonAdd> 
 */
object AdminProductGroup extends AdminProductGroup {
   def getCurrentProductGroup() : ProductGroup = {
     val id = S.param("pgID").openOr(null);
     if (id != null) {
    	 val catalogBean = lookupCatalog()
    	 catalogBean.findProductGroupById(java.lang.Long.valueOf(id))
     } else {
       null
     }
   } 
}
 
  
class AdminProductGroup extends BasicSnippet[ProductGroup] {

  object currentProductGroup extends SessionVar[ProductGroup](null)
  //object currentProductGroupRV extends RequestVar[Box[ProductGroup]](Empty)

  def listChildren(xhtml : NodeSeq) : NodeSeq = {
		  val catalogBean = lookupCatalog()
		  val list = Model.listToWrapper(catalogBean.findAllProductGroups(0, 20).asInstanceOf[java.util.List[ProductGroup]])
		  
		  list.flatMap(productGroup =>
		  bind("productGroup", xhtml,
                 "name" -> productGroup.getLabels().asInstanceOf[java.util.ArrayList[Label]].get(0).getLabel(),
			     "detail" ->
					  SHtml.link("/admin/productgroup/" + productGroup.getId().toString(),
							  () => currentProductGroup(productGroup),
							  Text(productGroup.getLabels().asInstanceOf[java.util.ArrayList[Label]].get(0).getLabel()))))
				  
  }

  def listProperties(xhtml : NodeSeq) : NodeSeq = {
	  val catalogBean = lookupCatalog()
      val propertyQuery = new PropertyQuery()
      //propertyQuery.setProductGroup()
	  val list = Model.listToWrapper(catalogBean.findAllProperties(0, 20, propertyQuery).asInstanceOf[java.util.List[Property]])

      list.flatMap(property =>
		  bind("productGroup", xhtml,
	             "name" -> property.getName(),
	             "type" -> property.getType().toString()))

  }
  
  def show(xhtml : NodeSeq) : NodeSeq = {
    val catalogBean = lookupCatalog()
    xhtml
  }
}
