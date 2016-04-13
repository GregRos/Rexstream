package rexstream.operations

/**
  * Created by GregRos on 09/04/2016.
  */
trait Conversion[TIn, TOut] {
    val out : Option[TIn => TOut]
    val in : Option[TOut => TIn]
}
