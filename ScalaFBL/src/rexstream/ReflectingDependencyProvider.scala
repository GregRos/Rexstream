package rexstream
import rexstream.util._
/**
  * Created by GregRos on 04/06/2016.
  */
class ReflectingDependencyProvider() extends DependencyProvider {
    private def allMembers = for (x <- getClass.getAnnotatedMembers[DependencyAnnotation]) yield x

    def byName(name : String): Option[AnyRex] = {
        allMembers.find(m => m.getName == name).map(x => x.invoke(this).asInstanceOf[AnyRex])
    }
    override def iterator: Iterator[Dependency] = {
        allMembers.map(member => new Dependency(member.getName, member.invoke(this).asInstanceOf[AnyRex])).iterator
    }
}
