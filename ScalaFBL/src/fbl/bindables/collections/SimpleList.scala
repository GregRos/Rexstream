package fbl.bindables.collections

import fbl._
import fbl.bindables._
import fbl.collections._
import fbl.events._
import scala.collection._
/**
  * Created by GregRos on 06/02/2016.
  */

private[fbl] class SimpleList[T](ctor : Unit => ValueBindable[T]) extends BindingPointsList[T] {
    protected val inner = new AutoClosingList[ValueBindable[T]]
    inner.change ++= _change
    private val onBindableChanged = (SingleValueBindable : ValueBindable[T]) => (changeInfo : ContextualChangeInfo) => {
        val indexOf = inner.indexOf(SingleValueBindable)
        _change.raise(ItemMutated(indexOf, SingleValueBindable, changeInfo))
    }

    private def register(newBindable : ValueBindable[T]) = {
        newBindable.changed += onBindableChanged(newBindable)
    }

    override def insert(n : Int, init : ValueBindable[T] => Unit): ValueBindable[T] = {
        val newBindable = ctor(())
        init(newBindable)
        inner.insert(n, newBindable)
        register(newBindable)
        newBindable
    }

    override def remove(n : Int) : ValueBindable[T] = {
        val oldItem = inner(n)
        inner.remove(n)
        oldItem
    }

    override def length = inner.length

    override def clear() = {
        inner.clear()
    }

    override def apply(n : Int) ={
        inner(n)
    }

    override def iterator: Iterator[ValueBindable[T]] = inner.iterator

}
