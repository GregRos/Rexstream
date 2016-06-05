package rexstream.rex

import rexstream._
import rexstream.rex.collections.FilterList
import rexstream.rex.collections._
/**
  * Created by GregRos on 09/04/2016.
  */
class RexSort[T](val parent : RexVector[T], orderingProvider : RexScalar[Ordering[T]])
    extends StandardRexImplementation with RexVectorBackedByPointsList[T]
{
    val points = new SortList[T](parent.points, orderingProvider)
    val info = new StandardRexInfo(RexTypeNames.vectorSort, false)
    type MyDependency = DependencyProvider
    override val depends= new SourceAndProvider(parent, orderingProvider)
}
