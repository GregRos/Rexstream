package rexstream.rex

import rexstream.{RexVector, Source}

/**
  * Created by GregRos on 09/04/2016.
  */
private[rexstream] class RexVectorLink[T](inner : RexVector[T]) extends RexMap[T, T](inner, _.link_>) {

}
