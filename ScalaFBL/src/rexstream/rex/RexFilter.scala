package rexstream.rex

import rexstream._
import rexstream.rex.collections._

/**
  * Created by GregRos on 26/02/2016.
  */
class RexFilter[T](val parent : RexVector[T], map : RexTransform[T, Boolean])
    extends StandardRexImplementation with RexVectorBackedByPointsList[T] {

    override val points = new FilterList[T](parent.points, map)

    override type MyDependency = Source
    val depends = new Source(parent)
    val info = new StandardRexInfo(RexTypeNames.vectorFilter, false)
}

