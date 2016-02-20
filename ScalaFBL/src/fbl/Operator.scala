package fbl

/**
  * Created by GregRos on 20/02/2016.
  */
trait Operator[T, TOut] {
    def zero : TOut

    def plus(current : TOut, newValue : T) : TOut

    def minus(current : TOut, oldValue : T) : TOut

    def equal(a : TOut, b : TOut) : Boolean

    val isOrderInvariant : Boolean
}
