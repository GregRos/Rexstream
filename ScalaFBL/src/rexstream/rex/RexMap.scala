package rexstream.rex

import rexstream.rex.collections.MapList
import rexstream._

/**
  * Created by GregRos on 26/02/2016.
  */
class RexMap[TIn, TOut](parent : RexVector[TIn], map : RexTransform[TIn, TOut])
    extends BaseRexVector[TOut](new MapList[TIn, TOut](parent.points, map), parent)
{

}
