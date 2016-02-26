package fbl.bindables

import fbl._
import fbl.events.ContextualChangeInfo




/**
  * Created by GregRos on 06/02/2016.
  */
private[fbl] class VarBindable[T](initial: T) extends ValueBindable[T]  {

    def this() = {
        this(defaultValue[T])
    }

    override def canRead = true
    override def canWrite = true
    override def parent = null

    private var _value = initial

    override def value = if (isDisposed) throw Errors.Object_closed(this) else _value

    override def setValueWithContext( x: T)(context: ContextualChangeInfo) ={
        if (isDisposed) throw Errors.Object_closed(this)
        _value = x
        changed.raise(context)
    }
}
