package rexstream.rex

import rexstream._
import rexstream.rex.collections.{SimpleList, FilterList}

/**
  * Created by GregRos on 26/02/2016.
  */
class RexList[T]()
    extends DefaultRex with DefaultRexVector[T]
{
    val elements = new SimpleList[T](x => new RexVar[T]())
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.vectorList
    }
    override val depends = DependencyProvider.none

}
