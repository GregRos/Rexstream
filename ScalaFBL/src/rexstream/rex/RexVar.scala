package rexstream.rex

import rexstream.{_}
import rexstream.events.ContextualChangeData

/**
  * Created by GregRos on 06/02/2016.
  */
private[rexstream] class RexVar[T](initial: T, override val canWrite : Boolean = true, override val canRead : Boolean = true)
    extends RexScalar[T] with DefaultRex {

    def this() = {
        this(defaultValue[T])
    }
    override val depends = DependencyProvider.none
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.scalarVar
    }
    private var _value = initial

    override def value = {
        makeSureNotClosed()
        _value
    }

    override def value_=( x: T) ={
        makeSureNotClosed()
        _value = x
        changed.raise(null)
    }

    override def consistencyCheck(): Unit = ()
}
