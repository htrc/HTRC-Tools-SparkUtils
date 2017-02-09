package org.hathitrust.htrc.tools.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
  * Base class for all Spark tests.
  *
  * @author Nicola Ferraro
  */
class SparkTestBase extends FlatSpec with BeforeAndAfter {

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
