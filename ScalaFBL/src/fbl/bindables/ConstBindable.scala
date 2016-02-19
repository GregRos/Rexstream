package fbl.bindables

import fbl.Bindable
import fbl.events.ContextualChangeInfo

/**
  * Created by GregRos on 06/02/2016.
  */
class ConstBindable[T](override val value: T) extends Bindable[T] {
    override def setValueWithContext(x: T)(context: ContextualChangeInfo)  = throw CannotWriteException()
}
