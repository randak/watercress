Watercress
==========

Watercress is a tool to generate a styleguide based on comments within a CSS file. Inspired by KSS, but designed with further automation in mind. Written in Scala with a hint of Java.

Documentation
-------------

To view the latest Scala docs, check out the [API Documentation](http://randak.github.io/watercress/latest/api/#com.kristianrandall.watercress.package)

Basic Usage
-----------

Using the parsed sections in a template is quite straightforward. These examples are using the Play! framework.

### In the controller:

```
import com.kristianrandall.watercress._

object Styleguide extends Controller {
  def styles() = Action { implicit request =>
    val sections = Parser.parseFiles("app/assets/stylesheets/")

    Ok(views.html.styleguide.style(sections))
  }
}
```

### In the view:

```
@sections.map { section =>
  @section.sectioning.size match {
    case 1 => { <h2>@section.sectioning.mkString(".") @section.title</h2> }
    case 2 => { <h3>@section.sectioning.mkString(".") @section.title</h3> }
    case 3 => { <h4>@section.sectioning.mkString(".") @section.title</h4> }
    case _ => { <h3>@section.title</h3> }
  }
  <div class="description">@Html(section.description)</div>
  <div class="modifiers">
    <ul>
      @section.modifiers.map { modifier =>
        <li><strong>@modifier.name</strong> - @modifier.description</li>
      }
    </ul>
  </div>
}
```

Templating support is not yet complete, but it will allow templates for each modifier to be automatically generated.
