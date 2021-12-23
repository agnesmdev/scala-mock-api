lazy val root = (project in file("."))
  .enablePlugins(PlayService, PlayLayoutPlugin, SwaggerPlugin)
  .settings(
    name := "scala-mock-api",
    scalaVersion := "2.13.6",
    swaggerDomainNameSpaces := Seq("models"),
    libraryDependencies ++= Seq(
      guice,
      "net.codingwell" %% "scala-guice" % "5.0.1",
      "com.github.javafaker" % "javafaker" % "1.0.2",

      /** Dépendances pour le Swagger */
      "org.webjars" % "swagger-ui" % "3.43.0",

      /** Dépendances pour le framework Play */
      "com.typesafe.play" %% "play" % "2.8.8",

      /** Dépendance pour gérer le logging */
      "ch.qos.logback" % "logback-classic" % "1.2.5",

      /** Dépendances dans les classes de tests */
      "org.scalatest" %% "scalatest" % "3.2.9" % Test
    )
  )
