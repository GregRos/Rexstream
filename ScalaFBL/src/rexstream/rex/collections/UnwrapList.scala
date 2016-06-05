package rexstream.rex.collections

import rexstream.{RexScalar, RexVectorElements, AnyRex}
import rexstream.collections._
import rexstream.events._

import scala.collection.mutable._
/**
  * Exposes the elements of a `BindablesList[T]`, translating operations on the elements to operations on the bindables.
  *
  */
private[rexstream] class UnwrapList[T](source : RexVectorElements[T]) extends Buffer[T] with ItemChangedNotifier[T] {
    private val onBindableListChanged = (param : ItemChanged[RexScalar[T]]) => {
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

    source.change += onBindableListChanged



    private def insert(n : Int, item : T) : Boolean = {
        source.insert(n, b => b.value = item) != null
    }

    override def +=(elem: T): UnwrapList.this.type = {
        insert(length, elem)
        this
    }

    override def update(n: Int, newelem: T): Unit = {
        source(n).value= newelem
    }

    override def clear(): Unit = {
        source.clear()
    }

    override def length: Int = source.length

    override def remove(n: Int): T = {
        val oldValue = source(n).value
        source.remove(n)
        oldValue
    }

    override def +=:(elem: T): UnwrapList.this.type = {
        insert(0, elem)
        this
    }

    override def apply(n: Int): T = source(n).value

    override def insertAll(n: Int, elems: scala.Traversable[T]): Unit = {
        var i = n
        source.insertAll(n, elems.map(x => (b : RexScalar[T])=> b.value = x))
    }

    override def iterator: Iterator[T] = source.map(b => b.value).iterator
}
