package fbl
package object bindables {

    private case class DefaultBox[T]() {
        var default : T = _
    }

    private[fbl] def defaultValue[T] = {
        DefaultBox[T]().default
    }
}