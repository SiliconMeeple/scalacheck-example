# Introduction to property based testing

This is an [activator](https://typesafe.com/activator) tutorial to support a property based testing workshop at the [Scala Exchange 2015](http://scala.exchange).

If you plan to attend the workshop, please clone the repository and run:

```
sbt test
```

After successfully downloading the necessary libraries, it should end up with a bunch of failed tests, looking something like the following:

![Image of a successful sbt test](/images/scalacheck-example-image.png)

Please use the [gitter chatroom](https://gitter.im/HolyHaddock/scalacheck-example) for the project, or contact me via gmail or twitter if it does not.

There are likely to be updates between today and the workshop, but the libraries being used should not change, so running this now will save time and wi-fi on the day.

## REPL

To get started interactively investigating the scalacheck library, this project sets some default imports to get you startedin  the powerful [Ammonite REPL](https://lihaoyi.github.io/Ammonite/#Ammonite-REPL) just by running:

```
   sbt test:console
```



[![Join the chat at https://gitter.im/HolyHaddock/scalacheck-example](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/HolyHaddock/scalacheck-example?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
