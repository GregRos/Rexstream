package rexstream
import scala.collection._
import rexstream.events._
import rexstream.rex.StdVectorBinding
import rexstream.util._
/**
  * Created by GregRos on 26/02/2016.
  */
trait RexVector[T] extends mutable.Buffer[T] with AnyRex[ItemChanged[T]] {
    private var _binding : VectorBinding[T] = null
    override def toString = {
        s">{${this.map(_.toString).truncate(100, "...").mkString(", ")}}>"
    }

    def reset(newCollection : Traversable[T]): Unit

    def binding : VectorBinding[T] = _binding

    def binding_=(binding : VectorBinding[T]) : VectorBinding[T] = {
        if (_binding != null) {
            _binding.setTarget(null)
        }
        _binding = binding
        _binding.setTarget(this)
        _binding
    }

    protected[rexstream] val elements : RexVectorElements[T]
}
