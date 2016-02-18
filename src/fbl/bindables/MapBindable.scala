package fbl.bindables

import fbl.ListBindable
import fbl.bindables.collection._
import fbl.events.{Reset, BufferChanged}
import fbl.infrastructure._
import scala.collection._
import fbl._


class BufferBindable[T](inner : SimpleBindablesList[T]) extends ListBindable[T] {
    def this() = this(new SimpleBindablesList[T](Unit => new VarBindable[T]()))
    protected val itemsList = inner.unbind
    itemsList.change += ((sender, change) => changed.raise(change))
    changed.raise(Reset())
    override def setValueWithContext(x: mutable.Buffer[T])(context: ContextualChangeInfo): Unit = {
        val change =
            context match {
                case change : BufferChanged[T] => change
                case _ => Reset[T]()
            }
        change.applyOn(itemsList, Unit => x)
    }

    override def value: mutable.Buffer[T] = itemsList

    override val bindables: BindableList[T] = inner
}

class MapBindable[TIn, TOut](inner : ListBindable[TIn], convert : BindableMap[TIn, TOut]) extends ListBindable[TOut] {
    val outer = new MappingBindableList[TIn, TOut](inner.bindables, convert)
    val itemsList =  outer.unbind
    itemsList.change += ((sender : Any, change : BufferChanged[TOut]) => changed.raise(change))
    override val bindables = outer
    changed.raise(Reset())

    override def setValueWithContext(v : mutable.Buffer[TOut])(changeInfo : ContextualChangeInfo) = {
        val changeKind =
            changeInfo match {
                case (x : BufferChanged[TOut]) => x
                case _ => Reset[TOut]()
            }
        changeKind.applyOn(v, Unit => v)
    }

    override val parent = inner
    override def value = itemsList
}
