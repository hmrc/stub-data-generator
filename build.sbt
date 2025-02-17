val scala2_13 = "2.13.10"

val compileDependencies = Seq(
    "org.typelevel"       %% "simulacrum"      % "1.0.1",
    "com.chuusai"         %% "shapeless"       % "2.3.10",
    "org.typelevel"       %% "cats-core"       % "2.9.0",
    "org.scalacheck"      %% "scalacheck"      % "1.17.0",
    "io.chrisdavenport" %% "cats-scalacheck" % "0.3.2"
  )

val testDependencies = Seq(
    "org.scalatest"         %% "scalatest"       % "3.2.16"   % Test,
    "com.vladsch.flexmark"  %  "flexmark-all"    % "0.64.8"   % Test,
    "org.mockito"           %% "mockito-scala"   % "1.17.14"  % Test,
    "org.scalatestplus"     %% "scalacheck-1-17" % "3.2.16.0" % Test,
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
    dependencyOverrides +=  "org.typelevel"        %% "cats-core"  % "2.9.0",
    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 => List("-Ypartial-unification")
        case _                       => List("-Ymacro-annotations")
      }})
