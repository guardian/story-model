import sbtrelease._
import ReleaseStateTransformations._

val commonSettings = Seq(
  organization := "com.gu",
  scalaVersion := "2.11.8",
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
      releaseStepCommand("sonatypeReleaseAll"),
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
  .dependsOn(thrift)
  .settings(
    name := "story-model",
    description := "Story model",
    scroogeThriftSourceFolder in Compile := baseDirectory.value / "../thrift/src/main/thrift",
    scroogeThriftOutputFolder in Compile := sourceManaged.value,
    scroogeThriftSources in Compile ++= {
      (scroogeUnpackDeps in Compile).value.flatMap { dir => (dir ** "*.thrift").get }
    },
    libraryDependencies ++= Seq(
        "org.apache.thrift" % "libthrift" % "0.9.3",
        "com.twitter" %% "scrooge-core" % "4.5.0"
    ),
    scroogeThriftDependencies in Compile ++= Seq(
      "content-atom-model-thrift",
      "content-entity-thrift"
    ),
    managedSourceDirectories in Compile += (scroogeThriftOutputFolder in Compile).value,
    // Include the Thrift file in the published jar
    scroogePublishThrift in Compile := true
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
    libraryDependencies ++= Seq(
      "com.gu" % "content-atom-model-thrift" % "2.4.36"
    ),
    unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/thrift" }
  )


