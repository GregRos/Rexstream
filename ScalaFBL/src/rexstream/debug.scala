package rexstream

object debug {
    implicit class BindableExte(inner : AnyRex[_]) {
        def consistencyCheck(): Unit = {
            inner.consistencyCheck()
        }
    }
}