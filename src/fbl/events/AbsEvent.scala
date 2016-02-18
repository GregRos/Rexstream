package fbl.events

/**
  * Created by GregRos on 06/02/2016.
  */
trait AbsEvent[TOwner, TParam] extends AutoCloseable {
    final def +=(f: (TOwner, TParam) => Unit): AutoCloseable = {
        this.subscribe(f)
    }

    final def -=(f: (TOwner, TParam) => Unit) = {
        this.unsubscribe(f)
    }

    private val onSubEventRaised = (owner : TOwner, param : TParam) => {
        raise(param)
    }

    final def ++=(otherEvent : AbsEvent[_ >: TOwner, _ >: TParam]) = {
        this += otherEvent.onSubEventRaised
    }

    final def --=(otherEvent : AbsEvent[_ >: TOwner, _ >: TParam]) = {
        this -= otherEvent.onSubEventRaised
    }

    def subscribe(f: (TOwner, TParam) => Unit): AutoCloseable

    def unsubscribe(f: (TOwner, TParam) => Unit): Unit

    protected def raise(param : TParam) : Unit
}

