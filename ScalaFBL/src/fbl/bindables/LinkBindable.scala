package fbl.bindables

import fbl.Bindable
import fbl.events.ContextualChangeInfo

/**
  * Created by GregRos on 13/02/2016.
  */
private[fbl] class LinkBindable[T](inner : Bindable[T]) extends Bindable[T] {
    inner.changed ++= changed
    override def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit = inner.setValueWithContext(x)(context)

    override def value: T = inner.value
}
