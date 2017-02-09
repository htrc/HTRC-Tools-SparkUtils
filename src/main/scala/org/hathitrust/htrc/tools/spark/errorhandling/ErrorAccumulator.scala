package org.hathitrust.htrc.tools.spark.errorhandling

import java.io.{BufferedWriter, OutputStreamWriter}

import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.util.CollectionAccumulator
import org.hathitrust.htrc.tools.spark.Arm._

import scala.collection.JavaConversions._
import scala.io.Codec

/**
  * Class used to accumulate errors generated during RDD manipulations
  * via the tryMap() and tryFlatMap() operations.
  *
  * @param f  Converter from element type to accumulator key type
  * @param sc The current Spark context
  * @tparam T The element type for the element that generated the error
  * @tparam V The key type for storing in the accumulator
  */
class ErrorAccumulator[T, V](f: T => V)
                            (@transient private implicit val sc: SparkContext) extends Serializable {
  /**
    * The backing Spark accumulator.
    */
  private val acc: CollectionAccumulator[(V, Throwable)] = sc.collectionAccumulator[(V, Throwable)]

  /**
    * Adds a new error pair to the accumulator.
    *
    * @param elem The element that generated the error
    * @param t    The exception thrown
    */
  private[spark] def add(elem: T, t: Throwable): Unit = acc.add(f(elem) -> t)

  /**
    * Clears the accumulator content.
    */
  def clear(): Unit = acc.reset()

  /**
    * Checks whether the accumulator is empty.
    *
    * @return True if empty, False otherwise
    */
  def isEmpty: Boolean = acc.isZero

  /**
    * Checks whether the accumulator is non-empty.
    *
    * @return True if non-empty, False otherwise
    */
  def nonEmpty: Boolean = !isEmpty

  /**
    * Returns the error pairs stored in the accumulator.
    * Note: This can only be done on the driver.
    *
    * @return The sequence of error pairs
    */
  def errors: Seq[(V, Throwable)] = acc.value

  /**
    * Saves the errors in this accumulator to a tab-separated (TSV) file.
    *
    * @param path               The path where to save the error pairs (can be on HDFS)
    * @param exceptionFormatter A formatter for converting Throwable to String
    * @param codec              (Optional) The codec to use
    */
  def saveErrors(path: Path, exceptionFormatter: Throwable => String = ExceptionUtils.getStackTrace)
                (implicit codec: Codec = Codec.UTF8): Unit = {
    val fileSystem = FileSystem.get(sc.hadoopConfiguration)
    using(fileSystem.create(path, true)) { stream =>
      val out = new BufferedWriter(new OutputStreamWriter(stream, codec.charSet))
      out.write(toString(exceptionFormatter))
    }
  }

  /**
    * Returns the string representation of the error pairs in this accumulator.
    *
    * @param exceptionFormatter A formatter for converting Throwable to String
    * @return The string representation of the error pairs in this accumulator
    */
  def toString(exceptionFormatter: Throwable => String): String = {
    val sb = new StringBuilder
    for ((elem, error) <- errors) {
      sb.append(s"$elem\t${exceptionFormatter(error)}\n")
    }

    sb.toString()
  }

  /**
    * Returns the string representation of the error pairs in this accumulator.
    *
    * @return The string representation of the error pairs in this accumulator
    */
  override def toString: String = toString(ExceptionUtils.getStackTrace)
}