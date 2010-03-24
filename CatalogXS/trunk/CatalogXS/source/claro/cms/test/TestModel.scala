package claro.cms.test

import claro.common.util.ParseXML
import claro.common.util.Conversions._
import claro.cms.Conversions._
import claro.cms.CMS
import claro.cms.blog.{Blog,BlogEntry,Comment}

object Test {

  lazy val testBlog = new Blog {
    title = "Welcome to my Blog"
    blogEntries ::= new BlogEntry { 
      title = "Another Blog Entry"
      text = "What else do I have to say?"
    }
    blogEntries ::= new BlogEntry { 
      title = "My First Blog Entry!"
      text = "This is \nthe <i>content</i> of my blog entry."
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
        "blog" -> testBlog -> "blog"
      )
      case blog : Blog => Bindings(
        "title" -> blog.title,
        "entries" -> blog.blogEntries -> "entry"
      )
      case entry : BlogEntry => Bindings(
        "title" -> entry.title,
        "date" -> entry.date,
        "text" -> ParseXML(entry.text),
        "comments" -> entry.comments -> "comment"
      )
      case comment: Comment => Bindings(
        "author" -> comment.author
      )
    }
  }
}

