package fbl.bindables

import fbl._
import fbl.events.ContextualChangeInfo




/**
  * Created by GregRos on 06/02/2016.
  */
private[fbl] class VarBindable[T](initial: T) extends Bindable[T]  {

    def this() = {
        this(defaultValue[T])

    }

    private var _value = initial

    override def value = _value

    override def setValueWithContext( x: T)(context: ContextualChangeInfo) ={
        _value = x
        changed.raise(context)
    }
}
