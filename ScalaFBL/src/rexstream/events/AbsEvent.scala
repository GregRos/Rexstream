package rexstream.events

import rexstream.collections._

import scala.collection.mutable

/**
  * Created by GregRos on 06/02/2016.
  */
trait AbsEvent[TParam] extends AutoCloseable {
    final def +=(f: (TParam) => Unit): AutoCloseable = {
        this.subscribe(f)
    }

    final def -=(f: (TParam) => Unit) = {
        this.unsubscribe(f)
    }

    private val onSubEventRaised = (param : TParam) => {
        raise(param)
    }

    final def ++=(otherEvent : AbsEvent[ _ >: TParam]) = {
        this += otherEvent.onSubEventRaised
    }

    final def --=(otherEvent : AbsEvent[_ >: TParam]) = {
        this -= otherEvent.onSubEventRaised
    }

    def subscribe(f: (TParam) => Unit): AutoCloseable

    def unsubscribe(f: (TParam) => Unit): Unit

    protected def raise(param : TParam) : Unit
}




