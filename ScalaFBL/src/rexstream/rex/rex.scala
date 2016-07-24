package rexstream

import rexstream.events.{ItemChanged, ScalarChangeData}

package object rex {

    private case class DefaultBox[T]() {
        var default : T = _
    }

    private[rexstream] def defaultValue[T] = {
        DefaultBox[T]().default
    }

    private[rexstream] type DefaultRexWithScalarChange = DefaultRex[ScalarChangeData]
    private[rexstream] type DefaultRexWithVectorChange[T] = DefaultRex[ItemChanged[T]]
}