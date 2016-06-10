package rexstream
import scala.collection._
import rexstream.events._
import rexstream.util._
/**
  * Created by GregRos on 26/02/2016.
  */
trait RexVector[T] extends mutable.Buffer[T] with AnyRex {
    override type MyChangeInfo = ItemChanged[T]

    override def toString = {
        s">{${this.map(_.toString).truncate(100, "...").mkString(", ")}}>"
    }

    protected[rexstream] val elements : RexVectorElements[T]
}
