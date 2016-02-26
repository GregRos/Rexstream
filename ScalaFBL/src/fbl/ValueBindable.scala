package fbl

import fbl.events._

/**
  * Created by GregRos on 26/02/2016.
  */
trait ValueBindable[T] extends AnyBindable {
    type ChangeInfo = ContextualChangeInfo

    def value : T

    def setValueWithContext(x : T)(context : ContextualChangeInfo) : Unit

    final def value_=(x : T) = setValueWithContext(x)(EmptyChangeInfo)

    override def toString = {
        s">${value.toString}<"
    }
}
