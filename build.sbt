name := "Watercress"

version := "0.3.2"

scalaVersion := "2.11.1"

scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")

libraryDependencies += "org.specs2" % "specs2_2.11" % "2.4.2"
