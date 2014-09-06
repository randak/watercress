package com.kristianrandall.watercress

class Line(string: String) {
  def isComment: Boolean = commentType.equals(CommentType.NONE)
  def isSingle: Boolean = commentType.equals(CommentType.SINGLE)
  def commentType: CommentType = {
    string match {
      case s if s matches """^//(.*)""" => CommentType.SINGLE
      case s if s matches """^/\*{1,2}(.*)""" => CommentType.MULTI_START
      case s if s matches """^\*/""" => CommentType.MULTI_END
      case s if s matches """^\*(.*)""" => CommentType.MULTI_MIDDLE
      case _ => CommentType.NONE
    }
  }
  def suffix: String = "^(\\/{2}|\\/\\*{1,2}|\\*(\\/){0,1})".r.replaceAllIn(string, "").trim
}