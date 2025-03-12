val scala2_13 = "3.3.4"


val compileDependencies = Seq(
    "org.typelevel"       %% "simulacrum"      % "1.0.1" cross CrossVersion.for3Use2_13,
    "org.typelevel"       %% "shapeless3-deriving"       % "3.5.0",
    "org.typelevel"       %% "cats-core"       % "2.13.0",
    "org.scalacheck"      %% "scalacheck"      % "1.18.1",
    "io.chrisdavenport" %% "cats-scalacheck" % "0.3.2"
  )

val testDependencies = Seq(
    "org.scalatest"         %% "scalatest"       % "3.2.19"   % Test,
    "com.vladsch.flexmark"  %  "flexmark-all"    % "0.64.8"   % Test,
    "org.scalatestplus"     %% "scalacheck-1-17" % "3.2.18.0" % Test,
    "io.chrisdavenport" %% "cats-scalacheck" % "0.3.2" % Test
  )

lazy val stubDataGenerator = Project("stub-data-generator", file("."))
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    majorVersion := 1,
    isPublicArtefact := true,
    scalaVersion := scala2_13,
    crossScalaVersions := Seq(scala2_13),
    libraryDependencies ++= compileDependencies ++ testDependencies ++ {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 =>
          List(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
        case _                       => Nil
      }},
    dependencyOverrides +=  "org.typelevel"        %% "cats-core"  % "2.13.0",
    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 => List("-Ypartial-unification")
        case _                       => List("-Ymacro-annotations")
      }})
