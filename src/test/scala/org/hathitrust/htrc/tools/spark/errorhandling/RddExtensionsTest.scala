package org.hathitrust.htrc.tools.spark.errorhandling

import org.hathitrust.htrc.tools.spark.SparkTestBase
import org.hathitrust.htrc.tools.spark.errorhandling.RddExtensions._
import org.scalatest.matchers.should

/**
  * Basic tests of the library.
  *
  * @author Nicola Ferraro
  * @author Boris Capitanu
  */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class RddExtensionsTest extends SparkTestBase with should.Matchers {

  "mapIgnoreErrors and flatMapIgnoreErrors" should  "silently drop errors" in {
    val sum = sc.parallelize(1 to 10)
      .mapIgnoreErrors(i => if (i % 2 == 0) throw new Exception(s"$i is even") else i)
      .flatMapIgnoreErrors(i => if (i % 3 == 0) throw new Exception(s"$i is div by 3") else Some(i))
      .reduce(_ + _)

    sum should be (13)
  }

  "mapValuesIgnoreErrors and flatMapValuesIgnoreErrors" should  "silently drop errors" in {
    val sum = sc.parallelize(1 to 10).zipWithIndex().map(_.swap)
      .mapValuesIgnoreErrors(i => if (i % 2 == 0) throw new Exception(s"$i is even") else i)
      .flatMapValuesIgnoreErrors(i => if (i % 3 == 0) throw new Exception(s"$i is div by 3") else Some(i))
      .map(_._2)
      .reduce(_ + _)

    sum should be (13)
  }

  "tryMap and tryFlatMap" should "correctly accumulate errors" in {
    val acc = new ErrorAccumulator[Int, Int](identity)(sc)

    val sum = sc.parallelize(1 to 12)
      .tryMap(i => {
        if (i % 2 == 0)
          i
        else
          throw new RuntimeException("A")
      })(acc)
      .tryFlatMap(i => {
        if (i % 3 == 0)
          Some(i)
        else
          throw new Exception("B")
      })(acc)
      .reduce(_ + _)

    // only 6 and 12 are ok
    sum should be (18)
    acc.errors should have size 10
    acc.errors.map { case (_, e) => e }.count(_.getMessage == "A") should be (6)
    acc.errors.map { case (_, e) => e }.count(_.getMessage == "B") should be (4)
  }

  "tryMapValues and tryFlatMapValues" should "correctly accumulate errors" in {
    val acc = new ErrorAccumulator[(Long, Int), Int](_._2)(sc)

    val sum = sc.parallelize(1 to 12).zipWithIndex().map(_.swap)
      .tryMapValues(i => {
        if (i % 2 == 0)
          i
        else
          throw new RuntimeException("A")
      })(acc)
      .tryFlatMapValues(i => {
        if (i % 3 == 0)
          Some(i)
        else
          throw new Exception("B")
      })(acc)
      .map(_._2)
      .reduce(_ + _)

    // only 6 and 12 are ok
    sum should be (18)
    acc.errors should have size 10
    acc.errors.map { case (_, e) => e }.count(_.getMessage == "A") should be (6)
    acc.errors.map { case (_, e) => e }.count(_.getMessage == "B") should be (4)
  }

  "forEachIgnoreErrors" should "silently drop errors" in {
    val acc = sc.longAccumulator
    sc.parallelize(1 to 10)
      .forEachIgnoreErrors {
        case n if n % 3 == 0 =>
          throw new IllegalArgumentException(n.toString)
        case n => acc.add(n)
      }

    acc.value should be (37)
  }

  "tryForEach" should "correctly accumulate errors" in {
    val acc = new ErrorAccumulator[Int, Int](identity)(sc)

    sc.parallelize(1 to 10)
      .tryForEach {
        case n if n % 3 == 0 =>
          throw new Exception(n.toString)
        case _ =>
      }(acc)

    acc.errors should have size 3
    acc.errors.map { case (_, e) => e.getMessage.toInt } should contain theSameElementsAs List(3, 6, 9)
  }

  "Multiple accumulators" can "be defined" in {
    val acc1 = new ErrorAccumulator[Int, Int](identity)(sc)
    val acc2 = new ErrorAccumulator[Int, Int](identity)(sc)

    val sum = sc.parallelize(1 to 12)
      .tryMap(i => {
        if (i % 2 == 0)
          i
        else
          throw new RuntimeException("A")
      })(acc1)
      .tryFlatMap(i => {
        if (i % 3 == 0)
          Some(i)
        else
          throw new Exception("B")
      })(acc2)
      .reduce(_ + _)

    // only 6 and 12 are ok
    sum should be (18)
    acc1 should not be empty
    acc2 should not be empty
    acc1.errors should have size 6
    acc2.errors should have size 4
    acc1.errors.map { case (_, e) => e }.count(_.getMessage == "A") should be (6)
    acc2.errors.map { case (_, e) => e }.count(_.getMessage == "B") should be (4)
  }

}
