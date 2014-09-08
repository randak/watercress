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
