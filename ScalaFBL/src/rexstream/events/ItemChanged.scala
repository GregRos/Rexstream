package rexstream.events
import scala.collection._
/**
  * Created by GregRos on 19/02/2016.
  */

trait ItemChangedNotifier[T] {
    protected val _change = new Event[ItemChanged[T]]()
    val change : AbsEvent[ItemChanged[T]] = _change
}


abstract class ItemChanged[T] extends ContextualChangeData {
    def applyOn(target : mutable.Buffer[T], items : Unit => Seq[T]) = {
        this match {
            case ItemAdded(index, newValue) => target.insert(index, newValue)
            case ItemRemoved(index) => target.remove(index)
            case ItemUpdated(index, newValue) => target.update(index, newValue)
            case ItemMutated(_,_,_) =>
            case Reset() =>{
                target.clear()
                target ++= items(())
            }
        }
    }
}

case class ItemRemoved[T](index : Int) extends ItemChanged[T]

case class ItemUpdated[T](index : Int, newValue : T) extends ItemChanged[T]

case class ItemMutated[T](index : Int, value: T, changeInfo: ContextualChangeData) extends ItemChanged[T]

case class Reset[T]() extends ItemChanged[T]

case class ItemAdded[T](index : Int, newValues : T) extends ItemChanged[T]
