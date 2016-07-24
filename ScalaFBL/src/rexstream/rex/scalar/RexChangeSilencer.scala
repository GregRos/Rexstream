package rexstream.rex.scalar

import rexstream._
import rexstream.events.ScalarChangeData
import rexstream.rex._

/**
  * Created by GregRos on 13/02/2016.
  */
private[rexstream] class RexChangeSilencer[T](inner : RexScalar[T], filter : (RexScalar[T], ScalarChangeData) => Boolean)
    extends DefaultRexWithScalarChange with RexScalar[T] {
    val _rexToken = inner.changed += (data => {
        if (filter(inner, data)) {
            this.changed.raise(data);
        }
    })
    override def canRead = inner.canRead
    override def canWrite = inner.canWrite
    override val depends= DependencyProvider.source(inner)

    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.scalarChangeSilencer
    }
    override def value: T = {
        makeSureNotClosed()
        inner.value
    }

    override def value_=(v : T) = {
        inner.value = v
    }

    override def close(): Unit = {
        _rexToken.close()
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
