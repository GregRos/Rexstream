package rexstream.rex

import rexstream._
import rexstream.events.ContextualChangeData

/**
  * Created by GregRos on 13/02/2016.
  */
private[rexstream] class RexScalarLink[T](inner : RexScalar[T])
    extends StandardRexImplementation with RexScalar[T] {
    inner.changed ++= changed
    override def canRead = inner.canRead
    override def canWrite = inner.canWrite
    type MyDependency = Source
    override val depends= new Source(inner)
    val info = new StandardRexInfo(RexTypeNames.scalarLink, true)
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
