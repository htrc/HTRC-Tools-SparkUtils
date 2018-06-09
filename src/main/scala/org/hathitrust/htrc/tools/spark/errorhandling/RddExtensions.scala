package org.hathitrust.htrc.tools.spark.errorhandling

import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

/**
  * Extensions that add simple error-handling operations to RDDs
  *
  * @author Nicola Ferraro
  * @author Boris Capitanu
  */
object RddExtensions {

  implicit class RddWithTryFunctions[T: ClassTag](rdd: RDD[T]) {
    /**
      * Tries to map every element of the RDD using the supplied map function.
      * Exceptions encountered during the application of the map function are paired with their
      * related elements and are added to the supplied error accumulator, enabling their later
      * retrieval on the driver.
      *
      * @param f   The map function
      * @param acc The error accumulator (or null if errors should be silently dropped)
      * @tparam U The result type of the map function
      * @tparam V The key type that will be paired with the error and stored in the accumulator
      * @return The RDD containing the successfully mapped elements
      */
    def tryMap[U: ClassTag, V: ClassTag](f: T => U)(acc: ErrorAccumulator[T, V]): RDD[U] = {
      rdd.flatMap(e => Try(f(e)) match {
        case Success(result) => Some(result)
        case Failure(t) =>
          if (acc != null) {
            acc.add(e, t)
          }
          None
      })
    }

    /**
      * Tries to map every element of the RDD using the supplied map function.
      * Exceptions encountered during the application of the map function are silently ignored.
      *
      * @param f The map function
      * @tparam U The result type of the map function
      * @tparam V The key type that will be paired with the error and stored in the accumulator
      * @return The RDD containing the successfully mapped elements
      */
    @SuppressWarnings(Array("org.wartremover.warts.Null"))
    def mapIgnoreErrors[U: ClassTag, V: ClassTag](f: T => U): RDD[U] = tryMap[U, V](f)(acc = null)

    /**
      * Tries to flatMap every element of the RDD using the supplied map function.
      * Exceptions encountered during the application of the map function are paired with their
      * related elements and are added to the supplied error accumulator, enabling their later
      * retrieval on the driver.
      *
      * @param f   The map function
      * @param acc The error accumulator (or null if errors should be silently dropped)
      * @tparam U The result type of the map function
      * @tparam V The key type that will be paired with the error and stored in the accumulator
      * @return The RDD containing the successfully mapped elements
      */
    def tryFlatMap[U: ClassTag, V: ClassTag](f: T => TraversableOnce[U])
                                            (acc: ErrorAccumulator[T, V]): RDD[U] = {
      rdd.flatMap(e => Try(f(e)) match {
        case Success(result) => result
        case Failure(t) =>
          if (acc != null) {
            acc.add(e, t)
          }
          Nil
      })
    }

    /**
      * Tries to flatMap every element of the RDD using the supplied map function.
      * Exceptions encountered during the application of the map function are silently ignored.
      *
      * @param f The map function
      * @tparam U The result type of the map function
      * @tparam V The key type that will be paired with the error and stored in the accumulator
      * @return The RDD containing the successfully mapped elements
      */
    @SuppressWarnings(Array("org.wartremover.warts.Null"))
    def flatMapIgnoreErrors[U: ClassTag, V: ClassTag](f: T => TraversableOnce[U]): RDD[U] =
      tryFlatMap[U, V](f)(acc = null)
  }

}
