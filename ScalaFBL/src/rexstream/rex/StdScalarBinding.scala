package rexstream.rex

import rexstream.events._
import rexstream._



/**
  * Created by GregRos on 16/07/2016.
  */
private[rexstream] class StdScalarBinding[T](
    origin : RexScalar[T],
    priority : BindPriority.Value
) extends StdBinding[ScalarChangeData, RexScalar[T]] (origin, priority) with ScalarBinding[T] {
    final override def rectify(fromOrigin : Boolean, change: ScalarChangeData): Unit = {
        var value : T = defaultValue[T]
        val sourceWord = if (fromOrigin) "origin" else "target"
        val targetWord = if (fromOrigin) "target" else "origin"
        try {
            if (fromOrigin) {
                value = origin.value
            } else {
                value = target.value
            }
        }
        catch {
            case ex : Exception => {
                throw new BindingActionException(s"Tried to get the $sourceWord's value, but it threw an exception.", ex)
            }
        }
        try {
            if (fromOrigin) {
                target.value = value
            } else {
                origin.value = value
            }
        }
        catch {
            case ex : Exception => {
                throw new BindingActionException(s"Tried to set the $targetWord's value, but it threw an exception.", ex)
            }
        }
    }
}
