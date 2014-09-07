package com.kristianrandall.watercress

/** Modifier to append to the relevant CSS identifier
  *
  * @param name the modifier
  * @param description a description of the modifier's expected behavior
  */
case class Modifier(name: String, description: String) {
  /** Replace any pseudo classes with a real class name for style guide display.
    * Also cleans any dots off the front of class names.
    *
    * @return A class name. If the modifier was a pseudo-class, it will be converted.
    */
  def className = name.replace(".", " ").replace(":", " pseudo-class-").trim
}
