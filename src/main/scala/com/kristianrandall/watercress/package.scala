package com.kristianrandall

/**
 * Provides a parser for stylesheets that can be used to generate a style guide that stays up
 * to date with the CSS automatically.
 *
 * The main class is [[watercress.Parser]], which can be used as follows:
 *
 * {{{
 * scala> val sections = watercress.Parser.parseFile("/link/to/directory")
 * sections: List[com.kristianrandall.watercress.Section]
 * }}}
 *
 * or
 *
 * {{{
 * scala> val sections = watercress.Parser.parseText("css text")
 * sections: List[com.kristianrandall.watercress.Section]
 * }}}
 */
package object watercress {}
