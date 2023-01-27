val scala2_12 = "2.12.9"
val scala2_13 = "2.13.10"

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

val compileDependencies = PlayCrossCompilation.dependencies(
  shared = Seq(
    "org.typelevel" %% "simulacrum" % "1.0.1",
    "com.chuusai"          %% "shapeless"  % "2.3.3",
    "org.typelevel"        %% "cats-core"  % "2.9.0",
    "org.scalacheck" %% "scalacheck" % "1.17.0",
    "io.github.amrhassan"   %% "scalacheck-cats" % "0.4.0"
  ))

val testDependencies = PlayCrossCompilation.dependencies(
  shared = Seq(
    "org.scalatest"         %% "scalatest"     % "3.2.15"  % Test,
    "com.vladsch.flexmark"  %  "flexmark-all"  % "0.62.0" % Test,
    "org.mockito"           %% "mockito-scala" % "1.5.11"  % Test,
    "org.scalatestplus" %% "scalacheck-1-17" % "3.2.15.0" % Test
  ),
  play28 = Seq(
    "com.typesafe.play" %% "play-test"         % "2.8.8"  % Test,
    "com.typesafe.play" %% "play-specs2"       % "2.8.8"  % Test
  )
)

lazy val stubDataGenerator = Project("stub-data-generator", file("."))
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    isPublicArtefact := true,
    scalaVersion := scala2_12,
    crossScalaVersions := Seq(scala2_12, scala2_13),
    libraryDependencies ++= compileDependencies ++ testDependencies ++ {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 =>
          List(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
        case _                       => Nil
      }},
    dependencyOverrides +=  "org.typelevel"        %% "cats-core"  % "2.9.0",
    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 => Nil
        case _                       => List("-Ymacro-annotations")
      }}
  )
  .settings(PlayCrossCompilation.playCrossCompilationSettings)
