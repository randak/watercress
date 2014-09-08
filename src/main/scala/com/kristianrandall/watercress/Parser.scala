package com.kristianrandall.watercress

import java.io.File

import scala.io.Source
import scala.math.Ordering.Implicits._

/** Generates a list of style guide [[Section]] objects from CSS files.
  *
  * Parser may read sections from a string or from a directory,
  * and will return an ordered list of [[Section]] instances based
  * on their section numbering.
  */
object Parser {

  /** Regex matching any comment block */
  private val blockPattern = """(/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/)|((//.*\n)+)(?!//)""".r

  /** Regex matching the comment characters on a single line */
  private val commentCharPattern = """(^\**/*\**)|(\*/$)""".r

  /** Regex matching a comment subsection (noted by an @ symbol) */
  private val subsectionPattern = """@(\\@|[^@])*""".r

  /** Read all files in a given directory, and parse them as a single CSS stylesheet.
    *
    * @param dir the directory in which to look for stylesheets
    * @return a List of [[Section]] instances
    */
  def parseFiles(dir: String): List[Section] =
    parseString( // Read all files in a directory and convert to a string, then parse it
      new File(dir)
        .listFiles.toIterator
        .filter(_ isFile)
        .flatMap(Source fromFile)
        .mkString
    )

  /** Parses a CSS string into a list of [[Section]] instances.
    *
    * @param css a block of CSS with lines broken by \n
    * @return a List of [[Section]] instances
    */
  def parseString(css: String): List[Section] =
    blockPattern.findAllIn(css).map {
      _.split("\n")
        .map( commentCharPattern.replaceAllIn(_, "").trim )
        .mkString("\n")
    }.map(parseComment).toList.sortBy(r => r.numbering)

  /** Parses a comment and converts it to a [[Section]] object.
    *
    * @param comment The raw text of the CSS comment
    * @return A style guide [[Section]]
    */
  private def parseComment(comment: String): Section = {
    var numbering = Vector[Int]()
    var title, description, template = ""
    var modifiers = Vector[Modifier]()

    //Find all subsections starting with a @ in the comment block
    subsectionPattern.findAllIn(comment).foreach { str =>
      val subsection = str.split("\n")
      subsection.head match {
        // A line beginning with the format @1.1.1
        case s if s matches """@(\d\.)*\d(.*)""" =>
          numbering = """\d+""".r.findAllIn(s).toList.map(_.toInt).toVector
          title = """^@(\d\.)*\d""".r.replaceAllIn(s, "").trim
          description = subsection.drop(1).mkString("\n").replaceAllLiterally("""\@""", "@")

        // Template section
        case s if s matches """@template(.*)""" =>
          template = subsection.drop(1) mkString "\n"

        // Modifiers section
        case s if s matches """@modifiers(.*)""" =>
          modifiers = subsection.drop(1)
            .map { _.split(""" - """).map(_.trim) }
            .filter(_.size > 0)
            .map { modifier =>
            modifier.size match {
              case 1 => Modifier(modifier(0), "")
              case _ => Modifier(modifier(0), modifier(1))
            }
          }.toVector

        case _ => //ignore invalid blocks
      }
    }

    new Section(numbering, title, description, modifiers, template)
  }
}