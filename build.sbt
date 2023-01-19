val scala2_12 = "2.12.15"
val scala2_13 = "2.13.7"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

//initialCommands in console := """import org.scalacheck._; import uk.gov.hmrc.smartstub._"""

val compileDependencies = PlayCrossCompilation.dependencies(
  shared = Seq(
  "org.scalacheck"       %% "scalacheck" % "1.13.5",
  "com.github.mpilquist" %% "simulacrum" % "0.12.0",
  "com.chuusai"          %% "shapeless"  % "2.3.3",
  "org.typelevel"        %% "cats-core"  % "1.1.0",
  "org.scalatest"        %% "scalatest"  % "3.2.14"   % "test"
))

//val compileDependencies = PlayCrossCompilation.dependencies(
//  shared = Seq(
//    "com.github.ben-manes.caffeine" % "caffeine"  % "2.8.8",
//    "uk.gov.hmrc"        %% "crypto"              % "6.1.0"
//  ),
//  play28 = Seq(
//    "com.typesafe.play"  %% "play"                % "2.8.8",
//    "com.typesafe.play"  %% "filters-helpers"     % "2.8.8",
//    "com.typesafe.play"  %% "play-guice"          % "2.8.8",
//    "uk.gov.hmrc"        %% "http-verbs-play-28"  % "13.12.0"
//  )
//)
//
val testDependencies = PlayCrossCompilation.dependencies(
  shared = Seq(
    "org.scalatest"         %% "scalatest"     % "3.1.2"   % Test,
    "com.vladsch.flexmark"  %  "flexmark-all"  % "0.35.10" % Test,
    "org.mockito"           %% "mockito-scala" % "1.5.11"  % Test,
    "io.github.amrhassan"   %% "scalacheck-cats" % "0.3.4" % Test
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
    libraryDependencies ++= compileDependencies ++ testDependencies
//    libraryDependencies
  )
  .settings(PlayCrossCompilation.playCrossCompilationSettings)
