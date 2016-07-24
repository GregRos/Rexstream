package rexstream

import rexstream.events.{ContextualChangeData, ItemChanged, ScalarChangeData}
import rexstream.util._

object BindPriority extends Enumeration {
    val Target, Origin = Value
}
object BindState extends Enumeration {
    val Active, Closed, Ready = Value
}


/**
	* Created by GregRos on 06/02/2016.
	*/
trait Binding[TChange] extends AutoCloseable {
	def origin : AnyRex[TChange]

	def target : AnyRex[TChange]

    def priority : BindPriority.Value

    def state : BindState.Value

    final def isBound = this.target != null
}

trait ScalarBinding[T] extends Binding[ScalarChangeData] {
    def origin : RexScalar[T]
    def target : RexScalar[T]

    def setTarget(target : RexScalar[T])
}

trait VectorBinding[T] extends Binding[ItemChanged[T]] {
    def origin : RexVector[T]

    def target : RexVector[T]

    def setTarget(target : RexVector[T])
}


