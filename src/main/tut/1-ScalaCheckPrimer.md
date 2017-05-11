# Using ScalaCheck to generate data

**1**. **smart-stub-generator** is built upon ScalaCheck Generators, and
works on the same principle. First import the scalacheck components
and use it to randomly generate a persons name - 

```tut
import org.scalacheck._
val personName = Gen.alphaStr
personName.sample.foreach(println)
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
