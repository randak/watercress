Watercress
==========

Watercress is a tool to generate a style guide based on comments within a CSS file. Inspired by [KSS](http://warpspire.com/kss/),
but designed with further template automation in mind. Written in Scala.

Documentation
-------------

To view the latest Scala Doc, check out the [API Documentation](http://randak.github.io/watercress/latest/api/#com.kristianrandall.watercress.package)

Installation
------------
To install using [SBT](http://www.scala-sbt.org/) and the [Play Framework](https://www.playframework.com/), add this repository as a dependency:

```
lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(watercress)

lazy val watercress = uri("git://github.com/randak/watercress.git#0.6.1") //or 'master'
```

Basic Usage
-----------

Adding documentation to your CSS, LESS, or SASS/SCSS has never been simpler. Styleguide documentation is simply CSS comments
with special syntax.

### Section Syntax
The top level section should have a number, a title, and a description. The description will be stored with line breaks
preserved, so as to allow for presentation customization. Future versions may support markdown descriptions, though there
is currently nothing preventing the use of it right now, provided a markdown parser is also used.

```
/* @1 Section Heading 
A description of the section.
*/
```

### Subsection Syntax
A subsection's syntax is the same as the top level section, but with additional information - a template and a list of
modifier classes and pseudo-classes. Section depth is not limited, so sections such as 1.1.1.1.1 could theoretically be 
used.

```
/* @1.1 Descriptive Title
A description of the section

@modifiers
:hover - Description of the hover state
.other - Description of the modifier class

@template
<p class="{class}">Example rendering</p>
*/
```

The parser will iterate through these sections, and is capable of rendering a version of the template for each modifier
to the main class with the use of the `section.renderTemplates` method. Single-line comment syntax is preferred in 
preprocessors that support it. 

In LESS:

```
// @1.2 Alternate Comment Syntax
// Description of subsection
//
// @modifiers
// :hover - Description
```

### Template Generation
In order to populate the templates, the placeholder `{class}` must be used. The template renderer will replace this with
the appropriate modifier. It should be noted that pseudo-class modifiers will be replaced with a relevant class name that
must be targeted within your CSS. For example, `:hover` will be replaced with `pseudo-class-hover`. This may be changed
to a javascript implementation in future versions. 

### Optional Sections
The `@template` and `@modifiers` sections are optional, as are the descriptions of the modifiers. They are recommended,
however. 

### Order
The order of the sections in the stylesheet does not matter, as the output is sorted by the section numbers. However, it
will be easier to maintain if they are in the correct order. This is done in particular to support spreading comments across
multiple files.



Styleguide Generation
---------------------

This example uses the [Play Framework](https://www.playframework.com/). The data can be presented in other ways, but
this will provide a simple way to get started.

### conf/routes

```
# Style guide
GET         /styleguide/$id<[0-9]+>                        controllers.StyleGuide.page(id: Int = 1)
```

### app/controllers/StyleGuide

```
package controllers;

import play.api.mvc._

import com.kristianrandall.watercress._

object StyleGuide extends Controller {
  def page(index: Int) = Action { implicit request =>
    val sections = Parser.parseFiles("app/assets/stylesheets/")
    Ok(views.html.styleguide.style(index, sections))
  }
}
```

### app/views/styleguide/style.scala.html

```
@(page: Int, sections: List[com.kristianrandall.watercress.Section])(implicit request: RequestHeader)

@main("Style guide") {
    <aside>
        <h2><a href="@routes.StyleGuide.overview()">All sections</a></h2>
        <ol>
        @sections.filter(_.numbering.size == 1).map { section =>
            <li><a href="@routes.StyleGuide.page(section.numbering(0))">@section.title</a></li>
        }
        </ol>
    </aside>
    <div class="styleguide">
        <section>
            @sections.filter(_.page == page).map { section =>
              @defining(section.numbering.mkString(".") + ". " + section.title) { display =>
                  @section.numbering.size match {
                      case 1 => { <h2>@display</h2> }
                      case 2 => { <h3>@display</h3> }
                      case 3 => { <h4>@display</h4> }
                      case _ => { <h3>@display</h3> }
                  }
              }
              <div class="description">
                @Html(section.description)
              </div>
              <div class="modifiers">
                  <ul>
                      @section.modifiers.map { modifier =>
                          <li><strong>@modifier.name</strong> - @modifier.description</li>
                      }
                  </ul>
              </div>
              @if(!section.template.isEmpty) {
                  <code class="raw html">
                      @section.template
                  </code>
                  <div class="display">
                  @section.renderTemplates.map { template =>
                      <div>
                          @if(!template.modifier.name.isEmpty) {
                              <h5>@template.modifier.name</h5>
                          }
                          @Html(template.html)
                      </div>
                  }
                  </div>
                  
              }
            }
        </section>
    </div>
}
```
