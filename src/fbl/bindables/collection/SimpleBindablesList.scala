package fbl.bindables.collection

import fbl.Bindable
import fbl.bindables.BindableList
import fbl.events.{ObservableCollection, ItemMutated}
import fbl.infrastructure._
/**
  * Created by GregRos on 06/02/2016.
  */

class SimpleBindablesList[T](ctor : Unit => Bindable[T]) extends BindableList[T] {
    protected val inner = ObservableCollection.empty[Bindable[T]]
    inner.change ++= _change

    private val onBindableChanged = (bindable : Bindable[T], changeInfo : ContextualChangeInfo) => {
        val indexOf = inner.indexOf(bindable)
        _change.raise(ItemMutated(indexOf, bindable, changeInfo))
    }

    private def register(newBindable : Bindable[T]) = {
        newBindable.changed += onBindableChanged
    }

    private def unregister(newBindable : Bindable[T]) = {
        newBindable.changed -= onBindableChanged
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
        unregister(oldItem)
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
