package org.hathitrust.htrc.tools.spark.utils

import org.apache.spark.SparkContext

object Helper {

  def stopSparkAndExit(sc: SparkContext, exitCode: Int = 0): Unit = {
    try {
      sc.stop()
    }
    finally {
      System.exit(exitCode)
    }
  }

}
