package rexstream

object debug {
    implicit class BindableExte(inner : AnyRex) {
        def consistencyCheck(): Unit = {
            inner.consistencyCheck()
        }
    }
}