package fbl.bindables

import fbl.Bindable
import fbl.infrastructure.ContextualChangeInfo

/**
  * Created by GregRos on 13/02/2016.
  */
class LinkBindable[T](inner : Bindable[T]) extends Bindable[T] {
    inner.changed ++= changed
    override def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit = inner.setValueWithContext(x)(context)

    override def value: T = inner.value
}
