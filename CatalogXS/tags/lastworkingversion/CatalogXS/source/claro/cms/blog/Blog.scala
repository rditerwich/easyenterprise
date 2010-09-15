package claro.cms.blog

import java.util.Date
import scala.xml.NodeSeq

object Blogger {

}

class Blog {
  var title = ""
  var blogEntries = List[BlogEntry]()
}

class BlogEntry {
  var title = ""
  var date = new Date
  var text = ""
  var comments = List[Comment]()
}

class Comment {
  var author = ""
  var text = ""
}