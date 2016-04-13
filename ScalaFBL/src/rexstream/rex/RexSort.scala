package rexstream.rex

import rexstream._
import rexstream.rex.collections.FilterList
import rexstream.rex.collections._
/**
  * Created by GregRos on 09/04/2016.
  */
class RexSort[T](val parent : RexVector[T], orderingProvider : RexScalar[Ordering[T]])
    extends BaseRexVector[T](new SortList[T](parent.points, orderingProvider), parent)
{
    override val dependency = NaryDependency.sourceAndProvider(parent, orderingProvider)
}
