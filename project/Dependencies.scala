import sbt.Keys._
import sbt._

object Dependencies {

  def spark(version: String): Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"      % version % Provided
    )
  )

  def spark_dev(version: String): Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"      % version
    )
  )

}
