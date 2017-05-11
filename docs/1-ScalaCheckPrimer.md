# Using ScalaCheck to generate data

**1**. **smart-stub-generator** is built upon ScalaCheck Generators, and
works on the same principle. First import the scalacheck components
and use it to randomly generate a persons name - 

```scala
scala> import org.scalacheck._
import org.scalacheck._

scala> val personName = Gen.alphaStr
personName: org.scalacheck.Gen[String] = org.scalacheck.Gen$$anon$3@6730d2d0

scala> personName.sample.foreach(println)
zzClzboiogjGjjbrmgpgbBdh
```

We have a couple of problems with the test data that is
produced - 

1. The data is not particularly plausible - ScalaCheck is great at
   finding edge cases for testing, but not so good if you want
   plausible data for demonstrating or visualising. In this case the
   persons name, while of the correct type (a String) its value is not 
   likely, even amongst footballers children. 
2. The data is not stable - if you run the same code again you would
   get a different name being produced. 
