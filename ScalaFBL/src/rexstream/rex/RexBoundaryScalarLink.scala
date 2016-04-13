package rexstream.rex

import rexstream.RexScalar

/**
  * Created by GregRos on 08/04/2016.
  */
class RexBoundaryScalarLink[T](inner : RexScalar[T]) extends RexScalarLink[T](inner) {
    override def consistencyCheck(): Unit = {
        //this special boundary token stops validation
    }
}
