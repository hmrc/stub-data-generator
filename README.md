# Smart Stub Generator (a.k.a. The Font of Infinite Nonsense) 

![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.gov.hmrc/stub-data-generator_2.12/badge.svg?style=plastic)

**smart-stub-generator** is a tool to create data for testing.  It is intended for use within HMRC to help the 'stub' services — microservices that mimic the interfaces of production backend systems. 

The data the generator produces is intended to be plausible but fake eliminating the need to either manually craft test records or take real records and anonymize them. For example when generating names, the names will look real rather than random strings.

The library is built for use within Scala Play 2.5 application controllers, but can be used as easily on the REPL or backed by a RDBMS. 

## SBT Project Setup

**1**. Add the following to `build.sbt`:

```scala
addSbtPlugin("uk.gov.hmrc" %% "stub-data-generator" % "X.X.X")
```

## Documentation
1. [Using ScalaCheck to generate data](docs/1-ScalaCheckPrimer.md)
2. [Procedurally generating data](docs/2-ProceduralGeneration.md)
3. [Composing data](docs/3-ComposingData.md)
4. [Enumerations and Patterns](docs/4-Enumerations.md)
5. [Mutable Data](docs/5-MutatingData.md)
6. [Simple Usage](docs/RichGen.md)
