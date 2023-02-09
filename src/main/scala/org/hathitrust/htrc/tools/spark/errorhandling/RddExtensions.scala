package org.hathitrust.htrc.tools.spark.errorhandling

import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import scala.collection.compat.IterableOnce

/**
  * Extensions that add simple error-handling operations to RDDs
  *
  * @author Nicola Ferraro
  * @author Boris Capitanu
  */
object RddExtensions {

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
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
    def tryMap[U: ClassTag, V](f: T => U)(acc: ErrorAccumulator[T, V]): RDD[U] = {
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
    def mapIgnoreErrors[U: ClassTag, V](f: T => U): RDD[U] = tryMap[U, V](f)(acc = null)

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
    def tryFlatMap[U: ClassTag, V](f: T => IterableOnce[U])
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
    def flatMapIgnoreErrors[U: ClassTag, V](f: T => IterableOnce[U]): RDD[U] =
      tryFlatMap[U, V](f)(acc = null)

    /**
      * Tries to invoke the given function on every element of this RDD.
      * Exceptions encountered during the application of the function are paired with their
      * related elements and are added to the supplied error accumulator, enabling their later
      * retrieval on the driver.
      *
      * @param f The function to apply
      * @param acc The error accumulator (or null if errors should be silently dropped)
      * @tparam U The key type that will be paired with the error and stored in the accumulator
      */
    def tryForEach[U](f: T => Unit)(acc: ErrorAccumulator[T, U]): Unit = {
      rdd.foreach(e => Try(f(e)) match {
        case Success(_) =>
        case Failure(t) =>
          if (acc != null) {
            acc.add(e, t)
          }
      })
    }

    /**
      * Tries to invoke the given function on every element of this RDD.
      * Exceptions encountered during the application of the function are silently ignored.
      *
      * @param f The function to apply
      * @tparam U The key type that will be paired with the error and stored in the accumulator
      */
    def forEachIgnoreErrors[U](f: T => Unit): Unit = tryForEach[U](f)(acc = null)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  implicit class PairRddWithTryFunctions[K: ClassTag, V: ClassTag](rdd: RDD[(K, V)]) {

    /**
      * Tries to map every value element of the PairRDD using the supplied map function.
      * Exceptions encountered during the application of the map function are paired with their
      * related elements and are added to the supplied error accumulator, enabling their later
      * retrieval on the driver.
      *
      * @param f   The map function
      * @param acc The error accumulator (or null if errors should be silently dropped)
      * @tparam U The result type of the map function
      * @tparam W The key type that will be paired with the error and stored in the accumulator
      * @return The PairRDD containing the successfully mapped elements
      */
    def tryMapValues[U: ClassTag, W](f: V => U)(acc: ErrorAccumulator[(K, V), W]): RDD[(K, U)] = {
      rdd.flatMap { case (k, e) => Try(f(e)) match {
        case Success(result) => Some(k -> result)
        case Failure(t) =>
          if (acc != null) {
            acc.add(k -> e, t)
          }
          None
      }}
    }

    /**
      * Tries to map every element value of the PairRDD using the supplied map function.
      * Exceptions encountered during the application of the map function are silently ignored.
      *
      * @param f The map function
      * @tparam U The result type of the map function
      * @tparam W The key type that will be paired with the error and stored in the accumulator
      * @return The PairRDD containing the successfully mapped elements
      */
    def mapValuesIgnoreErrors[U: ClassTag, W](f: V => U): RDD[(K, U)] = tryMapValues[U, W](f)(acc = null)

    /**
      * Tries to flatMap every element value of the PairRDD using the supplied map function.
      * Exceptions encountered during the application of the map function are paired with their
      * related elements and are added to the supplied error accumulator, enabling their later
      * retrieval on the driver.
      *
      * @param f   The map function
      * @param acc The error accumulator (or null if errors should be silently dropped)
      * @tparam U The result type of the map function
      * @tparam W The key type that will be paired with the error and stored in the accumulator
      * @return The PairRDD containing the successfully mapped elements
      */
    def tryFlatMapValues[U: ClassTag, W](f: V => IterableOnce[U])
                                        (acc: ErrorAccumulator[(K, V), W]): RDD[(K, U)] = {
      rdd.flatMap { case (k, e) => Try(f(e)) match {
        case Success(result) => result.iterator.map(k -> _)
        case Failure(t) =>
          if (acc != null) {
            acc.add(k -> e, t)
          }
          Nil
      }}
    }

    /**
      * Tries to flatMap every element value of the PairRDD using the supplied map function.
      * Exceptions encountered during the application of the map function are silently ignored.
      *
      * @param f The map function
      * @tparam U The result type of the map function
      * @tparam W The key type that will be paired with the error and stored in the accumulator
      * @return The PairRDD containing the successfully mapped elements
      */
    def flatMapValuesIgnoreErrors[U: ClassTag, W](f: V => IterableOnce[U]): RDD[(K, U)] =
      tryFlatMapValues[U, W](f)(acc = null)
  }
}
