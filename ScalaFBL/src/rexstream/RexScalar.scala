package rexstream

import rexstream.events._

/**
  * Created by GregRos on 26/02/2016.
  */
trait RexScalar[T] extends AnyRex {
    type ChangeInfo = ContextualChangeInfo

    def value : T

    def value_=(x : T)

    override def toString = {
        s">${value.toString}>"
    }
}
