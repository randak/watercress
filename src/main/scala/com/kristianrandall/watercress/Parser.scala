package com.kristianrandall.watercress

import java.io.File

import scala.collection.mutable
import scala.io.Source

object Parser {

//TODO  def parseString(css: String): List[Section]

  def parseFiles(dir: String): List[Section] = {
    var inBlock = false
    var prev: CommentType = CommentType.NONE
    var block: mutable.MutableList[String] = new mutable.MutableList[String]()
    var sections: mutable.MutableList[Section] = mutable.MutableList[Section]()

    new File(dir)
      .listFiles.toIterator
      .filter(_.isFile)
      .flatMap(Source.fromFile(_).getLines())
      .map( str => new Line(str) )
      .foreach{ line =>
        val cur = line.commentType
        if(inBlock) {
          if(cur.equals(CommentType.SINGLE) || cur.equals(CommentType.MULTI_MIDDLE)) {
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

    sections.toList
  }

  def makeSection(block: mutable.MutableList[String]): Section = {
    val b = block.filter{!_.isEmpty}

    b.size match {
      case 1 => new Section(b(0), "", List[Modifier]())
      case 2 => new Section(b(0), b(1), List[Modifier]())
      case _ =>
        val modifiers: List[Modifier] = b.drop(2).map { _.split("""-""").map{_.trim} }
          .filter{ _.size > 0 }
          .map { modifier => modifier.size match {
              case 1 => Modifier(modifier(0), "")
              case _ => Modifier(modifier(0), modifier(1))
            }
          }.toList

        new Section(b(0), b(1), modifiers)
    }
  }
}

case class Section(ref: String, description: String, modifiers: List[Modifier])

case class Modifier(name: String, description: String)