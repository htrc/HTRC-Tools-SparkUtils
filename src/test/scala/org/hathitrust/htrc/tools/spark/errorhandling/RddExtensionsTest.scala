package org.hathitrust.htrc.tools.spark.errorhandling

import org.hathitrust.htrc.tools.spark.SparkTestBase
import org.hathitrust.htrc.tools.spark.errorhandling.RddExtensions._
import org.scalatest.Matchers

/**
  * Basic tests of the library.
  *
  * @author Nicola Ferraro
  * @author Boris Capitanu
  */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class RddExtensionsTest extends SparkTestBase with Matchers {

  "Ignoring map and flatMap errors" should "work as expected in the normal case" in {
    val sum = sc.parallelize(1 to 10)
      .mapIgnoreErrors(i => i + 1)
      .flatMapIgnoreErrors(i => Some(i - 1))
      .reduce(_ + _)

    assert(sum == 55)
  }

  it should "ignore errors when they happen" in {
    val sum = sc.parallelize(1 to 10)
      .mapIgnoreErrors(i => if (i % 2 == 0) throw new Exception(s"$i is even") else i)
      .flatMapIgnoreErrors(i => if (i % 3 == 0) throw new Exception(s"$i is div by 3") else Some(i))
      .reduce(_ + _)

    assert(sum == 13)
  }

  "Using tryMap and tryFlatMap" should "accumulate errors correctly" in {
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
    assert(sum == 18)
    assert(acc.errors.size == 10)
    assert(acc.errors.map { case (_, e) => e }.count(_.getMessage == "A") == 6)
    assert(acc.errors.map { case (_, e) => e }.count(_.getMessage == "B") == 4)
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
    assert(sum == 18)
    assert(acc1.nonEmpty)
    assert(acc2.nonEmpty)
    assert(acc1.errors.size == 6)
    assert(acc2.errors.size == 4)
    assert(acc1.errors.map { case (_, e) => e }.forall(_.getMessage == "A"))
    assert(acc2.errors.map { case (_, e) => e }.forall(_.getMessage == "B"))
  }

}
