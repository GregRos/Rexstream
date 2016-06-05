package rexstream

import rexstream.events._

/**
  * Created by GregRos on 26/02/2016.
  */
trait RexScalar[T] extends AnyRex {
    override type MyChangeInfo = ScalarChangeData

    def value : T

    def value_=(x : T)

    def canRead : Boolean

    def canWrite : Boolean

    override def toString = {
        s">${value.toString}>"
    }
}
