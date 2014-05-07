name := "play2-curator-discovery"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "org.apache.curator" % "curator-x-discovery" % "2.4.2",
  "org.apache.curator" % "curator-test" % "2.4.2"
)

play.Project.playJavaSettings
