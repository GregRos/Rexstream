package rexstream.rex.vector

import rexstream._
import rexstream.rex.vector.collections._
import rexstream.rex.DefaultRex
/**
  * Created by GregRos on 09/04/2016.
  */
class RexSort[T](val parent : RexVector[T], orderingProvider : RexScalar[Ordering[T]])
    extends DefaultRexVector[T]
{
    val elements = new SortList[T](parent.elements, orderingProvider)
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.vectorSort
    }
    override val depends= DependencyProvider.sourceAndProvider(parent, orderingProvider)
}
