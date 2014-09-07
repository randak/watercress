name := "Watercress"

version := "0.4.0"

organization := "com.kristianrandall"

scalaVersion := "2.11.1"

scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")

libraryDependencies += "org.specs2" % "specs2_2.11" % "2.4.2"

site.settings

site.includeScaladoc()

ghpages.settings

git.remoteRepo := "git@github.com:randak/watercress.git"
