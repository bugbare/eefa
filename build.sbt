name := """eefa"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

compile in Test <<= PostCompile(Test)
testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-q", "-a")
