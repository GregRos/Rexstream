package fbl.bindables.collections

import fbl.{BindingPointsList, Bindable}
import fbl.collections._
import fbl.events._

import scala.collection.mutable._
/**
  * Exposes the elements of a `BindablesList[T]`, translating operations on the elements to operations on the bindables.
  *
  */
private[fbl] class UnwrapList[T](inner : BindingPointsList[T]) extends Buffer[T] with ItemChangedNotifier[T] {
    private val onBindableListChanged = (param : ItemChanged[Bindable[T]]) => {
        val myMessage : ItemChanged[T] =
            param match {
                case ItemAdded(i, v) => ItemAdded(i, v.value)
                case ItemRemoved(i) => ItemRemoved(i)
                case ItemUpdated(i, cur) => ItemUpdated(i, cur.value)
                case ItemMutated(i, bindable, _) => ItemUpdated(i, bindable.value)
                case Reset() => Reset()
                case _ => throw new Exception()
            }
        _change.raise(myMessage)
    }

    inner.change += onBindableListChanged

    private def insert(n : Int, item : T) = {
        _change.withSuppress(Unit => {
            inner.insert(n, b => b.value = item)
        })
        _change.raise(ItemAdded(n, item))
    }

    override def +=(elem: T): UnwrapList.this.type = {
        insert(length, elem)
        this
    }

    override def update(n: Int, newelem: T): Unit = {
        inner(n).value= newelem
    }

    override def clear(): Unit = {
        inner.clear()
    }

    override def length: Int = inner.length

    override def remove(n: Int): T = inner.remove(n).value

    override def +=:(elem: T): UnwrapList.this.type = {
        insert(0, elem)
        this
    }

    override def apply(n: Int): T = inner(n).value

    override def insertAll(n: Int, elems: scala.Traversable[T]): Unit = {
        var i = 0
        for (item <- elems) {
            val preLen = length
            insert(i + n, item)
            i += length - preLen
        }
    }

    override def iterator: Iterator[T] = inner.map(b => b.value).iterator
}
