package com.kristianrandall.watercress

import java.io.File

import scala.collection.mutable
import scala.io.Source

object Parser {

//TODO  def parseString(css: String): List[Section]

  def parseFiles(dir: String): List[Section] = {
    var inComment = false
    var prev: CommentType = CommentType.NONE
    var block: mutable.MutableList[String] = new mutable.MutableList[String]()

    var sections: mutable.MutableList[Section] = mutable.MutableList[Section]()

    new File(dir)
      .listFiles.toIterator
      .filter(_.isFile)
      .flatMap(Source.fromFile(_).getLines)
      .map( str => new Line(str) )
      .foreach{ line =>
        prev match {
          case CommentType.NONE => {
            inComment = false
            line.commentType match {
              case CommentType.SINGLE | CommentType.MULTI_START => block = mutable.MutableList(line.suffix)
              case _ =>
            }
          }
          case CommentType.SINGLE => {
            inComment = true
            line.commentType match {
              case CommentType.SINGLE => block += line.suffix
              case _ => sections += makeSection(block); block = new mutable.MutableList[String]
            }
          }
          case CommentType.MULTI_START => {
            inComment = true
            line.commentType match {
              case CommentType.MULTI_MIDDLE | CommentType.NONE | CommentType.MULTI_END => block += line.suffix
              case _ =>
            }
          }
          case CommentType.MULTI_MIDDLE => {
            inComment = true
            line.commentType match {
              case CommentType.MULTI_END => sections += makeSection(block); block = new mutable.MutableList[String]
              case CommentType.MULTI_MIDDLE => block += line.suffix
              case _ =>
            }
          }
          case CommentType.MULTI_END => {
            inComment = false
            sections += makeSection(block)
            block = new mutable.MutableList[String]
          }
        }

        prev = line.commentType
      }

    sections.toList
  }

  def makeSection(block: mutable.MutableList[String]): Section = {
    val b = block.filter{!_.isEmpty}

    b.size match {
      case 1 => new Section(b(0), "", List[Modifier]())
      case 2 => new Section(b(0), b(1), List[Modifier]())
      case _ => {
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
}

case class Section(ref: String, description: String, modifiers: List[Modifier])

case class Modifier(name: String, description: String)