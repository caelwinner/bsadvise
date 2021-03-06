resolvers ++= Seq(
  Resolver.url("sbt-plugin-releases2", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  "coda" at "http://repo.codahale.com"
)


resolvers += Classpaths.typesafeResolver

addSbtPlugin("io.spray" % "sbt-revolver" % "0.6.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.1")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.3")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.4.0")

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.3.0")