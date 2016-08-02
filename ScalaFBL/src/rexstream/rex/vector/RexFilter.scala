package rexstream.rex.vector

import rexstream._
import rexstream.rex.vector.collections._
import rexstream.rex.DefaultRex

/**
  * Created by GregRos on 26/02/2016.
  */
class RexFilter[T](val parent : RexVector[T], map : RexTransform[T, Boolean])
    extends DefaultRexVector[T] {

    override val elements = new FilterList[T](parent.elements, map)

    val depends = DependencyProvider.source(parent)
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.vectorFilter
    }

    override def consistencyCheck(): Unit = {
        val recalculated = parent.map(_.const_>).filter(x => map(x).value).map(x => x.value)
        if (!recalculated.equals(this.toSeq)) {
            throw Errors.Inconsistency_found("Manually filtered sequence doesn't match cached sequence.")
        }
        parent.consistencyCheck()
    }
}

