package org.hathitrust.htrc.tools.spark

import scala.language.reflectiveCalls

/**
  * Simple automated resource management
  *
  * @author Boris Capitanu
  */
object Arm {
  /**
    * Automatically closes the specified resource at the conclusion of the code block,
    * even if exceptions are thrown inside the code block. Returns the result computed
    * in the code block.
    *
    * @param resource The resource to auto-close
    * @param f        The function that uses the resource
    * @tparam T The resource type
    * @tparam U The return type
    * @return The result computed in the code block
    */
  def using[T <: {def close()}, U](resource: T)(f: T => U): U = {
    try {
      f(resource)
    }
    finally {
      if (resource != null)
        resource.close()
    }
  }
}
