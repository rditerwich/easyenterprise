package agilexs.catalogxs.presentation.snippet

import java.util.ArrayList
import net.liftweb._ 
import http._ 
import SHtml._ 
import S._ 
 
import js._ 
import JsCmds._ 
 
import mapper._ 

import util._
import Helpers._

import scala.xml.{NodeSeq, Text, SpecialNode, Group} 

import agilexs.catalogxs.presentation.cache._
import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.businesslogic._
import agilexs.catalogxs.jpa.catalog._

class PropertySnippet extends BasicSnippet[Property] {
  private object selectedProperty extends RequestVar[Box[Property]](Empty)

  /**
    * Add a user
    */
  def add(xhtml: Group): NodeSeq = {
	var name = ""
	var label = ""
    var propertyType : PropertyType = PropertyType.String
    val propertyTypes = PropertyType.values().map(pt => (pt, pt.toString))
    val productGroup = AdminProductGroup.getCurrentProductGroup()

    def processEntry () = {
        val catalogBean = lookupCatalog()
        val p : Property = new Property();
        val pg : ArrayList[ProductGroup] = new ArrayList();
        pg.add(productGroup)
        p.setProductGroups(pg)
        p.setName(name)
        p.setType(propertyType)
        //p.setLabel()
        catalogBean.updateProperty(null, p)
    	S.notice("Property added")
	}
    bind("e", xhtml,
            "productGroupName" ->
              Text(LabelCache.getLabel(productGroup.getLabels(), productGroup.getName())),
			"name" -> SHtml.text(name, name= _),
			"label" -> SHtml.text(label, label = _),
			"type" -> SHtml.selectObj[PropertyType](
			  propertyTypes, Full(PropertyType.String), pt => propertyType = pt),
			"submit" -> SHtml.submit("Process", processEntry))
  }

  /**
   * Edit a user
   * /
  def edit(xhtml: Group): NodeSeq =
    selectedProperty.map(_.
   // get the form data for the user and when the form
   // is submitted, call the passed function.
   // That means, when the user submits the form,
   // the fields that were typed into will be populated into
   // "user" and "saveUser" will be called.  The
   // form fields are bound to the model's fields by this
   // call.
         toForm(Empty, saveUser _) ++ <tr>
           <td><a href="/simple/index.html">Cancel</a></td>
           <td><input type="submit" value="Save"/></td>
         </tr>

       // bail out if the ID is not supplied or the user's not found
     ) openOr {error("User not found"); redirectTo("/simple/index.html")}
*/
}
