package rexstream

import java.lang.annotation.Annotation

import scala.annotation.StaticAnnotation

/**
  * Created by GregRos on 09/04/2016.
  */

case class MetadataAnnotation() extends StaticAnnotation with java.lang.annotation.Annotation {
    override def annotationType(): Class[_ <: Annotation] = classOf[MetadataAnnotation]
}
