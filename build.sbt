name := """scalacheck-example"""

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
  "com.lihaoyi" % "ammonite-repl" % "0.5.0" % "test" cross CrossVersion.full
)



initialCommands in Test in console :=
  """ammonite.repl.Repl.run("import org.scalacheck.Prop._; import org.scalacheck._")
  """.stripMargin

fork in run := true
