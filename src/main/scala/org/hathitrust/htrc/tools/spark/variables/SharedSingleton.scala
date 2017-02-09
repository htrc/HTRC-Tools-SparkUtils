package org.hathitrust.htrc.tools.spark.variables

import java.util.UUID

import scala.collection.concurrent.TrieMap
import scala.reflect.ClassTag

/**
  * Holds a variable shared among all workers that behaves like a local singleton.
  * Useful to use non-serializable objects in Spark closures that maintain state across tasks.
  *
  * @author Nicola Ferraro
  * @author Boris Capitanu
  */
class SharedSingleton[T: ClassTag](constructor: => T) extends AnyRef with Serializable {

  import SharedSingleton.singletonPool

  protected val uuid: String = UUID.randomUUID().toString

  @transient private lazy val instance: T =
    singletonPool.getOrElseUpdate(uuid, constructor).asInstanceOf[T]

  def get: T = instance
}

object SharedSingleton {
  private val singletonPool = new TrieMap[String, Any]()

  def apply[T: ClassTag](constructor: => T): SharedSingleton[T] = new SharedSingleton[T](constructor)

  def poolSize: Int = singletonPool.size

  def poolClear(): Unit = singletonPool.clear()
}