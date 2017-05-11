# Smart Stub Generator (a.k.a. The Font of Infinite Nonsense) 

**smart-stub-generator** is a tool to facilitate the creation of data for testing. It is named as such because it is intended for use within HMRC to help the 'stub' services - those that mimic the interfaces of production backend systems. 

The stub generator can be used to create all sorts of plausible, but fake data eliminating the need to manually craft test records or pseudonymise real records. It is built for use by HMRC within Play application controllers, but can be used as easily on the REPL or backed by a RDBMS. 

## SBT Project Setup

**1**. Add the following to `build.sbt`:

```scala
addSbtPlugin("hmrc" %% "stub-data-generator" % "0.1.0")
```

## Documentation
1. [Using ScalaCheck to generate data](docs/1-ScalaCheckPrimer.md)
2. [Procedurally generating data](docs/2-ProceduralGeneration.md)
3. [Composing data](docs/3-ComposingData.md)
4. [Enumerations and Patterns](docs/4-Enumerations.md)
5. [Mutable Data](docs/5-MutatingData.md)
6. [Simple Usage](docs/RichGen.md)
