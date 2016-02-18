package fbl.bindables

import fbl.ListBindable
import fbl.bindables._
import fbl.bindables.collection._
import fbl.events.{Reset, BufferChanged}
import fbl.infrastructure._
import fbl._
import scala.collection.mutable

/**
  * Created by GregRos on 13/02/2016.
  */
class FilterBindable[T](inner : ListBindable[T], selector : BindableMap[T, Boolean]) extends ListBindable[T] {
    val outer = new FilteringBindablesList[T](inner.bindables, selector)
    val itemsList =  outer.unbind
    itemsList.change += ((_, change) => changed.raise(change))
    override val bindables = outer
    changed.raise(Reset())
    override def setValueWithContext(v : mutable.Buffer[T])(changeInfo : ContextualChangeInfo) = {
        val changeKind =
            changeInfo match {
                case (x : BufferChanged[T]) => x
                case _ => Reset[T]()
            }
        changeKind.applyOn(v, Unit => v)
    }

    override def value = itemsList
}
