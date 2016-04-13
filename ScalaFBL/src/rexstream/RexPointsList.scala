package rexstream

import rexstream.rex.collections._
import rexstream.events._

import scala.collection.mutable

/**
  * List of `Bindable[T]`-type objects, which may be used for composing list-type bindables. Supports various specialized operations.
  *
  * This list reports when bindables are added, removed, or mutated.
  *
  * @tparam T The underlying value type of the bindables in this list.
  */
trait RexPointsList[T] extends ItemChangedNotifier[RexScalar[T]] with Seq[RexScalar[T]] with AutoCloseable  {

    private var _isClosed = false

    def isClosed = _isClosed

    /**
      * Constructs a new bindable, initializes it with the `init` initializer, adds it to the end of the list, and returns it.
      *
      * @param init An initializer called before the bindable is added to the list.
      * @return
      */
    final def add(init : RexScalar[T] => Unit): RexScalar[T] = {
        insert(length, init)
    }

    /**
      * Constructs a new bindable, initializes it with the `init` initializer, adds it to the `n`-th index in the list, and returns it.
      *
      * @param n The index at which to add.
      * @param init
      * @return
      */
    def insert(n: Int, init :RexScalar[T] => Unit): RexScalar[T]

    def insertAll(n : Int, inits : Traversable[RexScalar[T] => Unit]) : Traversable[RexScalar[T]] = {
        val items = mutable.MutableList[RexScalar[T]]()
        for (init <- inits) {
            val result: RexScalar[T] = insert(n + items.length, init)
            if (result != null) {
                items += result
            }
        }
        items
    }

    def remove(n: Int): RexScalar[T]

    def length : Int

    def clear() : Unit

    def apply(n: Int) : RexScalar[T]

    implicit lazy val unbind = new UnwrapList[T](this)

    def close(): Unit = {
        change.close()
        _isClosed = true
    }

    protected[rexstream] def validate() : Unit
}
