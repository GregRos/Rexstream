package fbl.bindables

import fbl.ListBindable
import fbl.bindables.collections._
import fbl.events._
import fbl._
import scala.collection.mutable

/**
  * Created by GregRos on 13/02/2016.
  */
private[fbl] class FilterListBindable[T](inner : ListBindable[T], selector : BindableMap[T, Boolean]) extends ListBindable[T] {
    val outer = new FilterList[T](inner.bindingPoints, selector)
    val itemsList =  outer.unbind
    itemsList.change += (change => changed.raise(change))
    override val bindingPoints = outer
    changed.raise(Reset())
    override def setValueWithContext(v : mutable.Buffer[T])(changeInfo : ContextualChangeInfo) = {
        val changeKind =
            changeInfo match {
                case (x : ItemChanged[T]) => x
                case _ => Reset[T]()
            }
        changeKind.applyOn(v, Unit => v)
    }

    override def value = itemsList
}
