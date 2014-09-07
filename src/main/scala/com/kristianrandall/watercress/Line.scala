package com.kristianrandall.watercress

/** Represents a line of CSS.
  *
  * @param string a string to convert into a [[Line]]
  */
class Line(string: String) {
  /** Returns the line's [[CommentType]] using regex
    *
    * @return CommentType based on regex matching
    */
  def commentType: CommentType = {
    string match {
      case s if s matches """^//(.*)""" => CommentType.SINGLE
      case s if s matches """^/\*{1,2}(.*)""" => CommentType.MULTI_START
      case s if s matches """^\*/""" => CommentType.MULTI_END
      case s if s matches """^\*(.*)""" => CommentType.MULTI_MIDDLE
      case _ => CommentType.NONE
    }
  }

  /** Strips the comment identifier (// or /* or * or */) and returns the comment
    *
    * @return the text of the comment
    */
  def suffix: String = """^\**/*\**""".r.replaceAllIn(string, "").trim
}