package fbl.bindables.collections

import fbl._
import fbl.bindables._
import fbl.collections._
import fbl.events._
import scala.collection._
/**
  * Created by GregRos on 06/02/2016.
  */

private[fbl] class SimpleList[T](ctor : Unit => Bindable[T]) extends BindingPointsList[T] {
    protected val inner = ObservableList.empty[Bindable[T]]
    inner.change ++= _change
    private val onBindableChanged = (bindable : Bindable[T]) => (changeInfo : ContextualChangeInfo) => {
        val indexOf = inner.indexOf(bindable)
        _change.raise(ItemMutated(indexOf, bindable, changeInfo))
    }

    private def register(newBindable : Bindable[T]) = {
        newBindable.changed += onBindableChanged(newBindable)
    }

    override def insert(n : Int, init : Bindable[T] => Unit): Bindable[T] = {
        val newBindable = ctor(())
        init(newBindable)
        inner.insert(n, newBindable)
        register(newBindable)
        newBindable
    }

    override def remove(n : Int) : Bindable[T] = {
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

    override def iterator: Iterator[Bindable[T]] = inner.iterator
}
