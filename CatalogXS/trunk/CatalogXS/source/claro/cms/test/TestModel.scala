package claro.cms.test

import claro.common.util.Conversions._
import claro.cms.Conversions._
import claro.cms.CMS
import claro.cms.blog.{Blog,BlogEntry,Comment}

object Test {

  lazy val testBlog = new Blog {
    title = "Welcome to my Blog"
    blogEntries ::= new BlogEntry { 
      title = "Another Blog Entry"
      text = xml.XML.loadString("<div>What else do I have to say?</div>")
    }
    blogEntries ::= new BlogEntry { 
      title = "My First Blog Entry!"
      text = xml.XML.loadString("<div>This is the <i>content</i> of my blog entry.</div>")
      comments ::= new Comment() {
        author = "barak"
      }
    }
  }
  
  def testBlog2 : Blog = testBlog
  
  def boot = {
    CMS.templateClasspath.append("claro.cms.test")
    CMS.entryPoints.append {
      case "showcase" :: Nil => Template("showcase")
    }
    CMS.bindings.append("test" -> Some(Test))
    CMS.objectBindings.append {
      case Test => Bindings(
        "blog" -> testBlog2 -> "blog"
      )
      case blog : Blog => Bindings(
        "title" -> blog.title,
        "entries" -> blog.blogEntries -> "entry"
      )
      case entry : BlogEntry => Bindings(
        "title" -> entry.title,
        "date" -> entry.date,
        "text" -> entry.text,
        "comments" -> entry.comments -> "comment"
      )
      case comment: Comment => Bindings(
        "author" -> comment.author
      )
    }
  }
}

