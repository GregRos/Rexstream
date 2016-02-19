package fbl

import fbl.bindables.collections._
import fbl.events._

/**
  * List of `Bindable[T]`-type objects, which may be used for composing list-type bindables. Supports various specialized operations.
  *
  * This list reports when bindables are added, removed, or mutated.
  *
  * @tparam T The underlying value type of the bindables in this list.
  */
trait BindingPointsList[T] extends ItemChangedNotifier[Bindable[T]] with Iterable[Bindable[T]] {
    /**
      * Constructs a new bindable, initializes it with the `init` initializer, adds it to the end of the list, and returns it.
      *
      * @param init An initializer called before the bindable is added to the list.
      * @return
      */
    final def add(init : Bindable[T] => Unit): Bindable[T] = {
        insert(length, init)
    }

    /**
      * Constructs a new bindable, initializes it with the `init` initializer, adds it to the `n`-th index in the list, and returns it.
      *
      * @param n The index at which to add.
      * @param init
      * @return
      */
    def insert(n: Int, init : Bindable[T] => Unit): Bindable[T]

    def remove(n: Int): Bindable[T]

    def length : Int

    def clear() : Unit

    def apply(n: Int) : Bindable[T]

    implicit lazy val unbind = new UnwrapList[T](this)
}
