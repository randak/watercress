package com.kristianrandall.watercress

import scala.collection.mutable.ListBuffer

/** A style guide section that represents a parsed CSS comment block.
  *
  * @param numbering section numbering (parsed from e.g. @1.1.1)
  * @param title title of the style guide section
  * @param description the description of the style guide section
  * @param modifiers a list of [[Modifier]] instances to apply within the template
  * @param template an HTML fragment indicating how to render the section
  */
case class Section(numbering: Vector[Int], title: String, description: String, modifiers: Vector[Modifier], template: String) {

  /** Generate a list of templates with the modifier class filled in.
    *
    * @return A list of strings that can be rendered as HTML
    */
  def renderTemplates: List[Template] = {
    val list: List[Modifier] = (ListBuffer[Modifier](Modifier("","")) ++= modifiers).toList
    list.map(m => Template(m, template))
  }

  def page: Int = numbering.head
}