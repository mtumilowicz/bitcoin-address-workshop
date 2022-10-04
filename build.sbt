ThisBuild / scalaVersion := "2.13.9"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "bitcoin-address-workshop",
    libraryDependencies ++= Seq(
      "org.bouncycastle" % "bcprov-jdk18on" % "1.72",
      "org.bouncycastle" % "bcpkix-jdk18on" % "1.72",
      "org.bitcoinj" % "bitcoinj-core" % "0.16.1",
      "com.beachape" %% "enumeratum" % "1.7.0",
      "org.bitcoinj" % "bitcoinj-core" % "0.16.1",
      "org.slf4j" % "slf4j-simple" % "2.0.3",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      "org.scalatestplus" %% "scalacheck-1-17" % "3.2.14.0" % Test
    ),
  )
