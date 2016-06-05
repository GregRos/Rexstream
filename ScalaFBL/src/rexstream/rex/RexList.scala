package rexstream.rex

import rexstream._
import rexstream.rex.collections.{SimpleList, FilterList}

/**
  * Created by GregRos on 26/02/2016.
  */
class RexList[T]()
    extends StandardRexImplementation with RexVectorBackedByPointsList[T]
{
    val points = new SimpleList[T](x => new RexVar[T]())
    val info = new StandardRexInfo(RexTypeNames.vectorList, false)
    override val depends = NoDependencies

}
