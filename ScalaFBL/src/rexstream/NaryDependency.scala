package rexstream

/**
  * Created by GregRos on 09/04/2016.
  */
class NaryDependency(inner : Map[String, AnyRex]) extends Iterable[AnyRex] {
    def byName(name : String): AnyRex = {
        inner(name)
    }
    override def iterator: scala.Iterator[AnyRex] = inner.values.iterator
}
