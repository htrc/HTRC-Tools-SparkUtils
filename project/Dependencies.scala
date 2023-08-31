import sbt.*
import sbt.Keys.*

object Dependencies {

  def spark(version: String): Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"      % version % Provided
    )
  )

}
