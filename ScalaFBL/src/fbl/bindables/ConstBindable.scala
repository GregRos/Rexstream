package fbl.bindables

import fbl.{Errors, Bindable}
import fbl.events.ContextualChangeInfo

/**
  * Created by GregRos on 06/02/2016.
  */
private[fbl] class ConstBindable[T](override val value: T) extends Bindable[T] {
    override def setValueWithContext(x: T)(context: ContextualChangeInfo)  = throw Errors.Cannot_write
}
