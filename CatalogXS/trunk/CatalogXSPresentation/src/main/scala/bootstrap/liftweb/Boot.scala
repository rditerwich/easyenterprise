package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import _root_.java.sql.{Connection, DriverManager}
import _root_.agilexs.catalogxs.presentation.model._
import _root_.javax.servlet.http.{HttpServletRequest}

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
//    if (!DB.jndiJdbcConnAvailable_?)
//      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    // where to search snippet
    LiftRules.addToPackages("agilexs.catalogxs.presentation")
    //We will take care of this
    //Schemifier.schemify(true, Log.infoF _, User)

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: User.sitemap
    LiftRules.setSiteMap(SiteMap(entries:_*))

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

// Set up a LoanWrapper to automatically instantiate and tear down the EntityManager on a per-request basis
    S.addAround(List(
      new LoanWrapper { 
		def apply[T] (f : => T): T = {
		  val em = Model.factory.createEntityManager()
	
		  // Add EM into S scope
		  Model.emVar.set(em)
		  
		  try {
		    f
		  } finally {
		    em.close()
		  }
		}
      }))
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HttpServletRequest) {
    req.setCharacterEncoding("UTF-8")
  }

}

/**
* Database connection calculation

object DBVendor extends ConnectionManager {

  def newConnection(name: ConnectionIdentifier): Can[Connection] = {
    try {
      Class.forName("org.postgresql.Driver")
      val dm = DriverManager.getConnection("jdbc:postgresql://localhost/dbname","username", "password")
      Full(dm)
    } catch {
      case e : Exception => e.printStackTrace; Empty
    }
  }

  def releaseConnection(conn: Connection) {conn.close}
}
*/

