package rexstream.rex

import rexstream.{NoDependencies, _}
import rexstream.events.ContextualChangeData

/**
  * Created by GregRos on 06/02/2016.
  */
private[rexstream] class RexVar[T](initial: T, override val canWrite : Boolean = true, override val canRead : Boolean = true)
    extends RexScalar[T] with StandardRexImplementation {

    def this() = {
        this(defaultValue[T])
    }
    override val depends = NoDependencies
    val info = new StandardRexInfo(RexTypeNames.scalarVar, false)
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
