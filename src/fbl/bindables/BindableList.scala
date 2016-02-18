package fbl.bindables

import fbl.Bindable
import fbl.bindables.collection.UnbindableList
import fbl.events.BufferChangedNotifier
import fbl.infrastructure._

/**
  * Created by GregRos on 12/02/2016.
  */
trait BindableList[T] extends BufferChangedNotifier[Bindable[T]] with Iterable[Bindable[T]] {
    final def add(init : Bindable[T] => Unit): Bindable[T] = {
        insert(length, init)
    }

    def insert(n: Int, init : Bindable[T] => Unit): Bindable[T]

    def remove(n: Int): Bindable[T]

    def length : Int

    def clear() : Unit

    def apply(n: Int) : Bindable[T]

    implicit lazy val unbind = new UnbindableList[T](this)
}
