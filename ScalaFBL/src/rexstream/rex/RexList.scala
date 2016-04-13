package rexstream.rex

import rexstream._
import rexstream.rex.collections.{SimpleList, FilterList}

/**
  * Created by GregRos on 26/02/2016.
  */
class RexList[T]()
    extends BaseRexVector[T](new SimpleList[T](x => new RexVar[T]()), null)
{
    override val dependency = NaryDependency.empty

}
