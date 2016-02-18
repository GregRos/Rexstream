package fbl
package object bindables {

    private case class DefaultBox[T]() {
        var default : T = _
    }

    def defaultValue[T] = {
        DefaultBox[T].default
    }
}