// Additional information on initialization
logLevel := Level.Warn

resolvers += Resolver.bintrayRepo("twittercsl", "sbt-plugins")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.11")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2-1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.5")

addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "19.3.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")

