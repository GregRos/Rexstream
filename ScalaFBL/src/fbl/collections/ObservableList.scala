package fbl.collections

import fbl.events._

import scala.collection.mutable


class ObservableCollection[T](private val inner : mutable.Buffer[T])  extends mutable.Buffer[T] with Seq[T] with ItemChangedNotifier[T] {
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
