Watercress
==========

Watercress is a tool to generate a style guide based on comments within a CSS file. Inspired by [KSS](http://warpspire.com/kss/),
but designed with further template automation in mind. Written in Scala with a hint of Java.

Documentation
-------------

To view the latest Scala docs, check out the [API Documentation](http://randak.github.io/watercress/latest/api/#com.kristianrandall.watercress.package)

Installation
------------
To install using SBT, add this repository as a dependency:

```
lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(watercress)

lazy val watercress = uri("git://github.com/randak/watercress.git#0.5.1") //or 'master'
```

Basic Usage
-----------

This example uses the [Play Framework](https://www.playframework.com/). It is merely for illustrative purposes. There are
many other options for display. Further examples will come pending a stable release.

### conf/routes

```
# Style guide
GET         /styleguide                                    controllers.StyleGuide.overview()
GET         /styleguide/$id<[0-9]+>                        controllers.StyleGuide.page(id: Int)
```

### app/controllers/StyleGuide

```
package controllers;

import play.api._
import play.api.mvc._

import com.kristianrandall.watercress._

object StyleGuide extends Controller {
  def overview() = Action { implicit request =>
    val sections = Parser.parseFiles("app/assets/stylesheets/").filter(_.sectioning.size == 1)

    Ok(views.html.styleguide.overview(str))
  }

  def page(index: Int) = Action { implicit request =>
    val sections = Parser.parseFiles("app/assets/stylesheets")
    val pageSections = sections.filter(_.sectioning(0) == index)
    val overview = sections.filter(_.sectioning.size == 1)

    Ok(views.html.styleguide.style(pageSections, overview))
  }
}
```


### app/views/styleguide/overview.scala.html

```
@(sections: List[com.kristianrandall.watercress.Section])(implicit request: RequestHeader)

@main("Style Guide") {
    <h1>EVIDENCE.com Style Guide</h1>
    <p>This guide is generated using <a href="https://github.com/randak/watercress/">Watercress</a>, a CSS documentation tool.</p>
    <ol>
      @overview.map { section =>
        <li><a href="@routes.StyleGuide.page(section.sectioning(0))">@section.title</a></li>
      }
    </ol>
}
```

### app/views/styleguide/style.scala.html

```
@(page: List[com.kristianrandall.watercress.Section], overview: List[com.kristianrandall.watercress.Section])(implicit request: RequestHeader)

@main("Style guide") {
    <aside>
        <h2><a href="@routes.StyleGuide.overview()">All sections</a></h2>
        <ol>
          @overview.map { section =>
            <li><a href="@routes.StyleGuide.page(section.sectioning(0))">@section.title</a></li>
          }
        </ol>
    </aside>

    @page.map { section =>
      @section.sectioning.size match {
        case 1 => { <h2>@section.sectioning.mkString(".") @section.title</h2> }
        case 2 => { <h3>@section.sectioning.mkString(".") @section.title</h3> }
        case 3 => { <h4>@section.sectioning.mkString(".") @section.title</h4> }
        case _ => { <h3>@section.title</h3> }
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
      <div class="display">
        @section.renderTemplates.map { template =>
            <div>
                <h5>@template.modifier.name</h5>
                @Html(template.html)
            </div>
        }
      </div>
    }
}
```