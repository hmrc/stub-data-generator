name := """smart-stub-web"""
organization := "hmrc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.15"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "hmrc" %% "stub-data-generator" % "0.2.0"

//resolvers += Resolver.bintrayRepo("hmrc", "maven")
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "hmrc.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "hmrc.binders._"

scalacOptions ++= Seq("-Xmax-classfile-name","128")
