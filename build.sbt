name := "play2-curator-service-discovery"

organization := "com.schwartech"

version := "1.0-SNAPSHOT"

publishTo := Some(Resolver.file("http://schwartech.github.com/m2repo/releases/",
  new File("/Users/jeff/dev/myprojects/schwartech.github.com/m2repo/releases")))

libraryDependencies ++= Seq(
  cache,
  "org.apache.curator" % "curator-x-discovery" % "2.7.0",
  "org.apache.curator" % "curator-test" % "2.7.0"
)

play.Project.playJavaSettings