package rexstream.rex.scalar

import rexstream._
import rexstream.rex._

/**
  * Created by GregRos on 13/02/2016.
  */
private[rexstream] class RexScalarLink[T](inner : RexScalar[T])
    extends DefaultRexWithScalarChange with RexScalar[T] {
    inner.changed ++= changed
    override def canRead = inner.canRead
    override def canWrite = inner.canWrite
    override val depends= DependencyProvider.source(inner)
    override val info = new RexInfo {
        val isLazy = true
        val isFunctional = false
        val rexType = RexTypeNames.scalarLink
    }
    override def value: T = {
        makeSureNotClosed()
        inner.value
    }

    override def value_=(v : T) = {
        inner.value = v
    }

    override def close(): Unit = {
        inner.changed --= changed
        super.close()
    }

    /**
      * Validates this bindable's integrity. This is a debugging feature.
      *
      * @return
      */
    override def consistencyCheck(): Unit = {
        //we recurse anyway in this case
        inner.consistencyCheck()
    }
}
