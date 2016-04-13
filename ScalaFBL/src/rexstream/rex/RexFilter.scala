package rexstream.rex

import rexstream._
import rexstream.rex.collections._

/**
  * Created by GregRos on 26/02/2016.
  */
class RexFilter[T](val parent : RexVector[T], map : RexTransform[T, Boolean])
    extends BaseRexVector[T](new FilterList[T](parent.points, map), parent)
{

}

