package fbl

import fbl.events.Event
import fbl.infrastructure._

import scala.collection.mutable._
/**
  * Created by GregRos on 06/02/2016.
  */
trait Bindable[T] extends AutoCloseable {
    private var _isDisposed = false

    def isDisposed = _isDisposed

    def canRead = true

    def canWrite = true

    def value: T

    final def value_=(x: T) = setValueWithContext(x)(EmptyChangeInfo)

    def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit

    def parent: Bindable[_] = null

    val changed = new Event[Bindable[T], ContextualChangeInfo](this)

    def close() = {
        changed.close()
        _isDisposed = true
    }
}

trait ListBindable[T] extends Bindable[Buffer[T]] {
    val bindables : BindableList[T]
}