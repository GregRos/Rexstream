package rexstream

import rexstream.events._
import rexstream.rex.StdVectorBinding

/**
  * Created by GregRos on 26/02/2016.
  */
trait RexScalar[T] extends AnyRex[ScalarChangeData] {
    private var _binding : ScalarBinding[T] = null
    def value : T

    def value_=(x : T)

    def canRead : Boolean

    def canWrite : Boolean

    def binding : ScalarBinding[T] = _binding

    def binding_=(binding : ScalarBinding[T]) : ScalarBinding[T] = {
        if (_binding != null) {
            _binding.setTarget(null)
        }
        _binding = binding
        _binding.setTarget(this)
        _binding
    }


    override def toString = {
        s">${value.toString}>"
    }
}
