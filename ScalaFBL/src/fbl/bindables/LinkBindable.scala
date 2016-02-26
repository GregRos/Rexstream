package fbl.bindables

import fbl._
import fbl.events.ContextualChangeInfo

/**
  * Created by GregRos on 13/02/2016.
  */
private[fbl] class LinkBindable[T](inner : ValueBindable[T]) extends ValueBindable[T] {
    inner.changed ++= changed
    override def canRead = inner.canRead
    override def canWrite = inner.canWrite
    override val parent = inner
    override def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit = {
        if (isDisposed) throw Errors.Object_closed(this)
        inner.setValueWithContext(x)(context)
    }

    override def value: T = inner.value
}
