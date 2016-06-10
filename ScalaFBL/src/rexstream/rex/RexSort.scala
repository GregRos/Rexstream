package rexstream.rex

import rexstream._
import rexstream.rex.collections.FilterList
import rexstream.rex.collections._
/**
  * Created by GregRos on 09/04/2016.
  */
class RexSort[T](val parent : RexVector[T], orderingProvider : RexScalar[Ordering[T]])
    extends DefaultRex with DefaultRexVector[T]
{
    val elements = new SortList[T](parent.elements, orderingProvider)
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.vectorSort
    }
    override val depends= DependencyProvider.sourceAndProvider(parent, orderingProvider)
}
