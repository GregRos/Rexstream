package fbl.bindables

import fbl._
import fbl.events.ContextualChangeInfo

/**
  * Created by GregRos on 06/02/2016.
  */
private[fbl] class ConstBindable[T](_value: T) extends ValueBindable[T] {
    override def canRead = true
    override def canWrite = false
    override val parent = null
    override def value = if (isDisposed) throw Errors.Object_closed(this) else _value
    override def setValueWithContext(x: T)(context: ContextualChangeInfo)  = {
        throw Errors.Cannot_write
    }
}
