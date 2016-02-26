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
trait BindingPointsList[T] extends ItemChangedNotifier[ValueBindable[T]] with Iterable[ValueBindable[T]]  {
    /**
      * Constructs a new bindable, initializes it with the `init` initializer, adds it to the end of the list, and returns it.
      *
      * @param init An initializer called before the bindable is added to the list.
      * @return
      */
    final def add(init : ValueBindable[T] => Unit): ValueBindable[T] = {
        insert(length, init)
    }

    /**
      * Constructs a new bindable, initializes it with the `init` initializer, adds it to the `n`-th index in the list, and returns it.
      *
      * @param n The index at which to add.
      * @param init
      * @return
      */
    def insert(n: Int, init :ValueBindable[T] => Unit): ValueBindable[T]

    def remove(n: Int): ValueBindable[T]

    def length : Int

    def clear() : Unit

    def apply(n: Int) : ValueBindable[T]

    implicit lazy val unbind = new UnwrapList[T](this)
}
