import com.typesafe.sbt.{GitBranchPrompt, GitVersioning}
import Dependencies._

showCurrentGitBranch

git.useGitDescribe := true

lazy val commonSettings = Seq(
  organization := "org.hathitrust.htrc",
  organizationName := "HathiTrust Research Center",
  organizationHomepage := Some(url("https://www.hathitrust.org/htrc")),
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq(
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-target:jvm-1.7"
  ),
  javacOptions ++= Seq(
    "-source", "1.7",
    "-target", "1.7"
  ),
  resolvers ++= Seq(
    "I3 Repository" at "http://nexus.htrc.illinois.edu/content/groups/public",
    Resolver.mavenLocal
  ),
  publishTo := {
    val nexus = "https://nexus.htrc.illinois.edu/"
    if (isSnapshot.value)
      Some("HTRC Snapshots Repository" at nexus + "content/repositories/snapshots")
    else
      Some("HTRC Releases Repository"  at nexus + "content/repositories/releases")
  },
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
    ("Git-Sha", git.gitHeadCommit.value.getOrElse("N/A")),
    ("Git-Branch", git.gitCurrentBranch.value),
    ("Git-Version", git.gitDescribedVersion.value.getOrElse("N/A")),
    ("Git-Dirty", git.gitUncommittedChanges.value.toString),
    ("Build-Date", new java.util.Date().toString)
  )
)

lazy val `spark-utils` = (project in file(".")).
  enablePlugins(GitVersioning, GitBranchPrompt).
  settings(commonSettings: _*).
  settings(spark_dev("2.1.0"): _*).
  settings(
    name := "spark-utils",
    description := "Suite of utility functions for helping build Apache Spark applications",
    licenses += "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    libraryDependencies ++= Seq(
      "org.scalacheck"                %% "scalacheck"           % "1.13.4"      % Test,
      "org.scalatest"                 %% "scalatest"            % "3.0.1"       % Test
    ),
    javaOptions ++= Seq(
      "-Xms512M", "-Xmx2048M",
      "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"
    ),
    parallelExecution in Test := false
  )
