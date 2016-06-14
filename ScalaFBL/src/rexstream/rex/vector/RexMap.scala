package rexstream.rex.vector

import rexstream._
import rexstream.rex.vector.collections.MapList
import rexstream.rex.DefaultRex

/**
  * Created by GregRos on 26/02/2016.
  */
class RexMap[TIn, TOut](parent : RexVector[TIn], map : RexTransform[TIn, TOut])
    extends DefaultRex with DefaultRexVector[TOut]
{
    override val elements = new MapList[TIn, TOut](parent.elements, map)
    val depends = DependencyProvider.source(parent)
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.vectorMap
    }
}
