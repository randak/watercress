import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import com.kristianrandall.watercress._

@RunWith(classOf[JUnitRunner])
class ParserSpec extends Specification {
  "Parser" should {
    "parse a single line comment block" in {
      val sections = Parser.parseFiles("src/test/resources")
      sections.size must equalTo(1)
      sections(0).description must equalTo("here it goes")
    }
  }
}
