package rexstream
package object rex {

    private case class DefaultBox[T]() {
        var default : T = _
    }

    private[rexstream] def defaultValue[T] = {
        DefaultBox[T]().default
    }
}