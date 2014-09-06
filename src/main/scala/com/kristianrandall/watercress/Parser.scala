package com.kristianrandall.watercress

import java.io.File

import scala.collection.mutable
import scala.io.Source
import scala.math.Ordering.Implicits._

object Parser {

  def parseString(css: String): List[Section] = {
    val lines = css.split("\n")
      .map(str => new Line(str.trim) )
      .toIterator

    parse(lines)
  }

  def parseFiles(dir: String): List[Section] = {
    var inBlock = false
    var prev: CommentType = CommentType.NONE
    var block: mutable.MutableList[String] = new mutable.MutableList[String]()
    var sections: mutable.MutableList[Section] = mutable.MutableList[Section]()

    val lines = new File(dir)
      .listFiles.toIterator
      .filter(_.isFile)
      .flatMap(Source.fromFile(_).getLines())
      .map( str => new Line(str.trim) )

    parse(lines)

  }

  def makeSection(block: mutable.MutableList[String]): Section = {
    val b: List[List[String]] = groupPrefix(block.toList)(_ matches """^\s*$""").map(_.filter(!_.isEmpty))

    var sectioning = List[Int]()
    var title = ""
    var description = ""
    var modifiers = List[Modifier]()
    var template = ""

    b.foreach { subBlock =>
        subBlock(0) match {
          case s if s matches """^@(\d\.)*\d(.*)""" =>
            sectioning = """\d+""".r.findAllIn(s).toList.map(_.toInt)
            title = """^@(\d\.)*\d""".r.replaceAllIn(s, "").trim
            description = "<p>" + subBlock.drop(1).mkString(" ") + "</p>"

          case s if s matches """@template(.*)""" =>
            template = subBlock.drop(1) mkString "\n"

          case s if s matches """@modifiers(.*)""" =>
            modifiers = subBlock.drop(1)
              .map { _.split("""-""").map(_.trim) }
              .filter(_.size > 0)
              .map { modifier =>
                modifier.size match {
                  case 1 => Modifier(modifier(0), "")
                  case _ => Modifier(modifier(0), modifier(1))
                }
              }

          case s => description = description + "<p>" + s + "</p>"
        }
      }

    new Section(sectioning, title, description, modifiers, template)
  }

  /** Returns shortest possible list of lists xss such that
    *   - xss.flatten == xs
    *   - No sublist in xss contains an element matching p in its tail
    */
  private def groupPrefix[T](xs: List[T])(p: T => Boolean): List[List[T]] = xs match {
    case List() => List()
    case x :: xs1 =>
      val (ys, zs) = xs1 span (!p(_))
      (x :: ys) :: groupPrefix(zs)(p)
  }

  private def parse(css: Iterator[Line]): List[Section] = {
    var inBlock = false
    var prev: CommentType = CommentType.NONE
    var block: mutable.MutableList[String] = new mutable.MutableList[String]()
    var sections: mutable.MutableList[Section] = mutable.MutableList[Section]()

    css.foreach { line =>
      val cur = line.commentType
      if(inBlock) {
        if(cur.equals(CommentType.SINGLE)
          || cur.equals(CommentType.MULTI_MIDDLE)
          || (cur.equals(CommentType.NONE) && !prev.equals(CommentType.SINGLE))) {

          block += line.suffix

        } else if((cur.equals(CommentType.NONE) && prev.equals(CommentType.SINGLE))
          || cur.equals(CommentType.MULTI_END)) {
          sections += makeSection(block)
          block = new mutable.MutableList[String]
          inBlock = false
        }
      } else if(cur.equals(CommentType.SINGLE) || cur.equals(CommentType.MULTI_START)) {
        block = mutable.MutableList(line.suffix)
        inBlock = true
      }
      prev = cur
    }

    sections.toList.sortBy(r => r.sectioning)
  }
}

case class Section(sectioning: List[Int], title: String, description: String, modifiers: List[Modifier], template: String)

case class Modifier(name: String, description: String)