package fbl.bindables

import fbl._
import fbl.bindables.collections._
import fbl.events._
import scala.collection._



class MapBindable[TIn, TOut](inner : ListBindable[TIn], convert : BindableMap[TIn, TOut]) extends ListBindable[TOut] {
    val outer = new MapList[TIn, TOut](inner.bindingPoints, convert)
    val itemsList =  outer.unbind
    itemsList.change += ((change : ItemChanged[TOut]) => changed.raise(change))
    override val bindingPoints = outer
    changed.raise(Reset())

    override def setValueWithContext(v : mutable.Buffer[TOut])(changeInfo : ContextualChangeInfo) = {
        val changeKind =
            changeInfo match {
                case (x : ItemChanged[TOut]) => x
                case _ => Reset[TOut]()
            }
        changeKind.applyOn(v, Unit => v)
    }

    override val parent = inner
    override def value = itemsList
}
