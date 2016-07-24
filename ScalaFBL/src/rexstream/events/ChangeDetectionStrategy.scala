package rexstream.events

/**
  * Created by GregRos on 29/06/2016.
  */
trait ChangeDetector[T <: ContextualChangeData] {
    def changed : AbsEvent[T]
}