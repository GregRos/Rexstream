package rexstream.rex

import rexstream._
import rexstream.rex.collections._

/**
  * Created by GregRos on 26/02/2016.
  */
class RexFilter[T](val parent : RexVector[T], map : RexTransform[T, Boolean])
    extends DefaultRex with DefaultRexVector[T] {

    override val elements = new FilterList[T](parent.elements, map)

    val depends = DependencyProvider.source(parent)
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.vectorFilter
    }
}

