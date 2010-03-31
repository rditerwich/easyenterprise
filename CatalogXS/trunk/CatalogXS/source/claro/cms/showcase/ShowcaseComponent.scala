package claro.cms.test

import claro.cms.blog._

class ShowcaseComponent extends Component {
  
  val prefix = "showcase"
  
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
  
  templateClasspath.append("claro.cms.test")
  
  entryPoints.append {
      case "showcase" :: Nil => Template("showcase")
    }

  bindings.append {
    case _ : ShowcaseComponent => Map(
        "blog" -> testBlog -> "blog"
      )
    case blog : Blog => Map(
        "title" -> blog.title,
        "entries" -> blog.blogEntries -> "entry"
      )
    case entry : BlogEntry => Map(
        "title" -> entry.title,
        "date" -> entry.date,
//        "text" -> ParseHtml(entry.text),
        "comments" -> entry.comments -> "comment"
      )
    case comment: Comment => Map(
        "author" -> comment.author
      )
    }
}
