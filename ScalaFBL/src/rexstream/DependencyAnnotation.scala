package rexstream

import scala.annotation.StaticAnnotation
import java.lang.annotation._
/**
  * Created by GregRos on 09/04/2016.
  */

case class DependencyAnnotation() extends StaticAnnotation with java.lang.annotation.Annotation {
    override def annotationType(): Class[_ <: Annotation] = classOf[DependencyAnnotation]
}
