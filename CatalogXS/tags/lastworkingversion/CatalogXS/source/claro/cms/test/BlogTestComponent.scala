package claro.cms.test

import claro.cms.blog._

object BlogTestComponent extends Component {
  
  val prefix = "test"
  
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
  
  bindings.append {
    case BlogTestComponent => Map(
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


object TestCms2 {
  def main(args : Array[String]) : Unit = {
    println("YES:")
  }
  
  def testList = {
    val xml = <test:blog>
  <h3><blog:title/></h3>
  <div style="border:2px solid grey">
  <blog:entries>
    <list:once><p><list:single>There is a single entry</list:single><list:plural>There are <list:size /> entries</list:plural></p></list:once>
    <list:skip-first><hr width="80%" size="1"/></list:skip-first>
    <h4><entry:title/></h4>
    <entry:text/>
    <p>This is entry <list:index/> of <list:size/></p>
    <p>This entry has <entry:comments><list:once><list:size/>  <list:single>comment</list:single><list:plural>comments</list:plural></list:once> 
    <list:first> (authors:</list:first><comment:author/><list:last>)</list:last>
    </entry:comments></p>
    <entry.text/>
    <list:last>Title of last entry (out of <list:size />) : <entry:title/></list:last>
  </blog:entries>
  </div>
</test:blog>
	xml
  }
}
