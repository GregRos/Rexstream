package rexstream.rex.vector

import rexstream.{DependencyProvider, RexInfo, RexTypeNames, RexVector}

/**
  * Created by GregRos on 09/04/2016.
  */
private[rexstream] class RexVectorLink[T](inner : RexVector[T]) extends RexMap[T, T](inner, _.link_>) {

    override val info = new RexInfo {
        val isLazy = true
        val isFunctional = false
        val rexType = RexTypeNames.vectorLink
    }

    override val depends = DependencyProvider.source(inner)
}
