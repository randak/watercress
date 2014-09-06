import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import com.kristianrandall.watercress._

@RunWith(classOf[JUnitRunner])
class ParserSpec extends Specification {
  "Parser" should {
    "parse a multi block file" in {
      val sections = Parser.parseFiles("src/test/resources/css")
      sections.size must equalTo(2)
    }

    "parse a multi line comment block" in {
      val sections = Parser.parseFiles("src/test/resources/css")
      sections(0).sectioning must equalTo(List(1,1,1))
      sections(0).title must equalTo("Button")
      sections(0).description must equalTo("<p>here it goes watch how it goes</p>")
      sections(0).modifiers must beEmpty
      sections(0).template must beEmpty
    }

    "parse a multi line comment block with no inner asterisks" in {
      val sections = Parser.parseFiles("src/test/resources/css")
      sections(1).sectioning must equalTo(List(1, 2))
      sections(1).title must equalTo("Big Button")
      sections(1).description must equalTo("<p>Here's a description without any filler</p>")
      sections(1).modifiers must equalTo(List(Modifier(":hover", "Just one of the many benefits of antigravity.")))
      sections(1).template must equalTo("<sample>Code</sample>")
    }

    "parse a single line comment block" in {
      val sections = Parser.parseFiles("src/test/resources/less")
      sections(0).sectioning(0) must equalTo(1)
      sections(0).title must equalTo("Buttons")
      sections(0).description must equalTo("<p>This is the buttons section</p><p>Here's a button</p>")
      sections(0).modifiers must beEmpty
      sections(0).template must equalTo("""<button class="{class}">Button (button.button)</button>"""+"\n"+"""<a href="#" class="button">Button (a.button)</a>""")
    }

    "parse comments in multiple files" in {
      val sections = Parser.parseFiles("src/test/resources/multifile")
      sections.size must equalTo(2)
      sections(0).title must equalTo("Main Section")
      sections(1).title must equalTo("Secondary Classes")
    }

    "order sections by numbering" in {
      val sections = Parser.parseFiles("src/test/resources/misordered")
      sections(0).sectioning(0) must equalTo(1)
    }
  }
}
