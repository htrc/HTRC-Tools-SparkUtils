package org.hathitrust.htrc.tools.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Base class for all Spark tests.
  *
  * @author Nicola Ferraro
  */
class SparkTestBase extends AnyFlatSpec with BeforeAndAfter {

  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.Null"))
  var sc: SparkContext = _

  before {
    val conf = new SparkConf()
      .setAppName("BasicIT")
      .set("spark.driver.allowMultipleContexts", "true")
      .setMaster("local[2]")

    sc = new SparkContext(conf)
  }

  after {
    sc.stop()
  }

}
