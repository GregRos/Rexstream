package rexstream.rex

import rexstream.events._
import rexstream._

/**
  * Created by GregRos on 16/07/2016.
  */
private[rexstream] class StdVectorBinding[T](
    origin : RexVector[T],
    priority : BindPriority.Value
) extends StdBinding[ItemChanged[T], RexVector[T]](origin, priority) with VectorBinding[T] {
    override final def rectify(fromOrigin : Boolean, change : ItemChanged[T]): Unit = {
        val target = if (fromOrigin) this.target else this.origin
        val source = if (fromOrigin) this.origin else this.target
        val sourceWord = if (fromOrigin) "origin" else "target"
        val targetWord = if (fromOrigin) "target" else "origin"
        try{
            change match {
                case ItemAdded(i, v) => target.insert(i, v)
                case ItemRemoved(i) => target.remove(i)
                case ItemUpdated(i, v) => target.update(i, v)
                case Reset() => {
                    target.reset(source)
                }
            }
        }
        catch {
            case ex : Exception => {
                throw new BindingActionException(s"Tried to make $sourceWord equal to $targetWord, but the operation threw an exception.", ex)
            }
        }
    }
}
