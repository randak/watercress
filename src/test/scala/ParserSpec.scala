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
      sections(0).modifiers.size must equalTo(1)
      sections(0).modifiers(0).name must equalTo(":hover")
      sections(0).modifiers(0).className must equalTo("pseudo-class-hover")
      sections(0).template must equalTo("""<button class="{class}">Button (button.button)</button>"""+"\n"
                                        +"""<a href="#" class="button {class}">Button (a.button)</a>""")
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

    "parse a CSS string" in {
      val sections = Parser.parseString("/* @1.1 CSS Test File \n This is a CSS test file that I have concocted. \n */")
      sections.size must equalTo(1)
      sections(0).sectioning must equalTo(List(1,1))
      sections(0).title must equalTo("CSS Test File")
      sections(0).description must equalTo("<p>This is a CSS test file that I have concocted.</p>")
    }

    "generate a list of templates based on modifiers" in {
      val sections = Parser.parseFiles("src/test/resources/less")

      sections(0).renderTemplates(0).modifier.name must equalTo("")
      sections(0).renderTemplates(0).html must equalTo("""<button class="">Button (button.button)</button>"""+"\n"
                                                      +"""<a href="#" class="button ">Button (a.button)</a>""")

      sections(0).renderTemplates(1).modifier.name must equalTo(":hover")
      sections(0).renderTemplates(1).html must equalTo("""<button class="pseudo-class-hover">Button (button.button)</button>"""+"\n"
                                                      +"""<a href="#" class="button pseudo-class-hover">Button (a.button)</a>""")

      //      sections(0).renderTemplates(0). must equalTo("""<button class="">Button (button.button)</button>"""+"\n"
//                                                  +"""<a href="#" class="button ">Button (a.button)</a>""")
//      sections(0).renderTemplates(1) must equalTo("""<button class="pseudo-class-hover">Button (button.button)</button>"""+"\n"
//                                                  +"""<a href="#" class="button pseudo-class-hover">Button (a.button)</a>""")
    }
  }
}
