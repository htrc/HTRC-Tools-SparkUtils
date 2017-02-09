package org.hathitrust.htrc.tools.spark.variables

import scala.reflect.ClassTag

/**
  * Holds a variable shared among all workers.
  * Enables the use non-serializable objects in Spark closures.
  *
  * @author Nicola Ferraro
  */
class SharedVariable[T: ClassTag](constructor: => T) extends AnyRef with Serializable {

  @transient private lazy val instance: T = constructor

  def get: T = instance

}

object SharedVariable {

  def apply[T: ClassTag](constructor: => T): SharedVariable[T] = new SharedVariable[T](constructor)

}