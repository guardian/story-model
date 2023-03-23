import sbtrelease._
import ReleaseStateTransformations._

val commonSettings = Seq(
  organization := "com.gu",
  scalaVersion := "2.12.8",
  crossScalaVersions := Seq("2.11.12", "2.12.8"),
  scmInfo := Some(ScmInfo(url("https://github.com/guardian/story-model"),
      "scm:git:git@github.com:guardian/story-model.git")),

  pomExtra := (
      <url>https://github.com/guardian/story-model</url>
      <developers>
          <developer>
              <id>mchv</id>
              <name>Mariot Chauvin</name>
              <url>https://github.com/mchv</url>
          </developer>
      </developers>
      ),

  licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),

  publishTo := sonatypePublishTo.value,
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      releaseStepCommand("sonatypeRelease"),
      pushChanges
  )
)

lazy val root = (project in file("."))
  .aggregate(thrift, scalaClasses)
  .settings(commonSettings)
  .settings(
    publishArtifact := false
  )

lazy val scalaClasses = (project in file("scala"))
  .settings(commonSettings)
  .settings(
    name := "story-model",
    description := "Story model",
    scroogeThriftSourceFolder in Compile := baseDirectory.value / "../thrift/src/main/thrift",
    scroogeThriftOutputFolder in Compile := sourceManaged.value,
    scroogeThriftDependencies in Compile ++= Seq(
      "content-entity-thrift",
      "content-atom-model-thrift"
    ),
    // Include the Thrift file in the published jar
    scroogePublishThrift in Compile := true,
    resolvers ++= Seq(Resolver.sonatypeRepo("public")),
    libraryDependencies ++= Seq(
        "org.apache.thrift" % "libthrift" % "0.18.1",
        "com.twitter" %% "scrooge-core" % "19.3.0",
        "com.gu" % "content-entity-thrift" % "2.0.1",
        "com.gu" % "content-atom-model-thrift" % "3.0.2",
        "com.gu" %% "content-atom-model" % "3.0.2",
        //adding annotation dependency to fix compilation error "Generated is not member of package javax.annotation"
        //as recommended at https://github.com/bazelbuild/rules_scala/pull/1090 to comptaible for java 11 or any higher than java 8
        "javax.annotation" % "javax.annotation-api" % "1.3.2",
    ),
  )

lazy val thrift = (project in file("thrift"))
  .disablePlugins(ScroogeSBT)
  .settings(commonSettings)
  .settings(
    name := "story-model-thrift",
    description := "Story model Thrift files",
    crossPaths := false,
    publishArtifact in packageDoc := false,
    publishArtifact in packageSrc := false,
    unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/thrift" }
  )


