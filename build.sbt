
val compileDependencies = Seq(
  "org.typelevel"  %% "cats-core"           % "2.13.0",
  "org.scalacheck" %% "scalacheck"          % "1.18.1"
)

val testDependencies = Seq(
  "org.scalatest"        %% "scalatest"           % "3.2.19",
  "com.vladsch.flexmark" %  "flexmark-all"        % "0.64.8",
  "org.scalatestplus"    %% "scalacheck-1-17"     % "3.2.18.0"
).map(_ % Test)

lazy val stubDataGenerator = Project("stub-data-generator", file("."))
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    majorVersion := 1,
    isPublicArtefact := true,
    scalaVersion := "3.3.4",
    libraryDependencies ++= compileDependencies ++ testDependencies,
    Compile / scalacOptions ++= List("-feature","-language:implicitConversions","-Wconf:msg=unused local definition:s","-Wconf:msg=unused import:s")
  )
