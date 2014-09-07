package com.kristianrandall.watercress

/** Renders an HTML fragment based on a [[Modifier]] and a template.
  *
  * @param modifier The modifier to render a template for.
  * @param raw The raw HTML fragment to render the template from.
  */
case class Template(modifier: Modifier, raw: String) {
  /** Insert the modifier into the raw template.
    *
    * @return A processed HTML fragment
    */
  def html: String = raw.replaceAllLiterally("{class}", modifier.className)
}