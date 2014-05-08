name := "CuratorClient"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.schwartech" %% "play2-curator-service-discovery" % "1.0-SNAPSHOT"
)

resolvers += (
  "SchwarTech GitHub Repository" at "http://schwartech.github.com/m2repo/releases/"
)

play.Project.playJavaSettings