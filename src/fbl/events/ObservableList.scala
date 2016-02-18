package fbl.events

import fbl.infrastructure._

import scala.collection.mutable

abstract class BufferChanged[T] extends ContextualChangeInfo {
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

case class ItemAdded[T](index : Int, newValues : T) extends BufferChanged[T]

case class ItemRemoved[T](index : Int) extends BufferChanged[T]

case class ItemUpdated[T](index : Int, newValue : T) extends BufferChanged[T]

case class ItemMutated[T](index : Int, value: T, changeInfo: ContextualChangeInfo) extends BufferChanged[T]

case class Reset[T]() extends BufferChanged[T]

trait BufferChangedNotifier[T] {
    protected val _change = new Event[BufferChangedNotifier[T], BufferChanged[T]](this)
    val change : AbsEvent[BufferChangedNotifier[T], BufferChanged[T]] = _change
}

class ObservableCollection[T](private val inner : mutable.Buffer[T])  extends mutable.Buffer[T] with Seq[T] with BufferChangedNotifier[T] {
    override def apply(n: Int): T = inner(n)

    override def update(n: Int, newelem: T): Unit = {
        inner.update(n, newelem)
        _change.raise(ItemUpdated(n, newelem))
    }



    override def clear(): Unit = {
        val items = inner.toList
        inner.clear()
        items.foreach (x => _change.raise(ItemRemoved(0)))
    }


    override def length: Int = inner.length

    override def remove(n: Int): T ={
        val item = inner(n)
        inner.remove(n)
        _change.raise(ItemRemoved(n))
        item
    }

    override def +=:(elem: T): this.type = {
        elem +=: inner
        _change.raise(ItemAdded(this.length - 1, elem))
        this
    }


    override def +=(elem: T): this.type = {
        inner += elem
        _change.raise(ItemAdded(0, elem))
        this
    }

    private def insertItem(n : Int, elem : T) : Unit = {
        inner.insert(n, elem)
        _change.raise(ItemAdded(n, elem))

    }

    override def insertAll(n: Int, elems: Traversable[T]): Unit = {
        elems.toSeq.zipWithIndex.foreach {case(item, i) => insertItem(n + i, item)}
    }


    override def iterator: Iterator[T] = inner.iterator
}

object ObservableCollection {
    def empty[T] = new ObservableCollection[T](mutable.ArrayBuffer.empty)
}
