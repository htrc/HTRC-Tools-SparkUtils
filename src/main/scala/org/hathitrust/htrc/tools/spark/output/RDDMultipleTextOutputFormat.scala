package org.hathitrust.htrc.tools.spark.output

import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat

/**
  * Write to multiple outputs dependent on the key, in a single Job
  */
class RDDMultipleTextOutputFormat extends MultipleTextOutputFormat[Any, Any] {
  override def generateActualKey(key: Any, value: Any): Any =
    NullWritable.get()

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def generateFileNameForKeyValue(key: Any, value: Any, name: String): String =
    key.asInstanceOf[String]
}

// Example use
//
// object Split {
//   def main(args: Array[String]) {
//     val conf = new SparkConf().setAppName("Split" + args(1))
//     val sc = new SparkContext(conf)
//     sc.textFile("input/path")
//       .map(a => (k, v)) // Your own implementation
//       .partitionBy(new HashPartitioner(num))
//       .saveAsHadoopFile("output/path", classOf[String], classOf[String],
//         classOf[RDDMultipleTextOutputFormat])
//     apache.spark.stop()
//   }
// }