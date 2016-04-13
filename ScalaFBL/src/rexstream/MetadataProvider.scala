package rexstream
import java.lang.annotation.Annotation

import scala.annotation.StaticAnnotation
import scala.collection.parallel._
import scala.reflect.runtime.universe._
/**
  * Created by GregRos on 09/04/2016.
  */

case class MetadataProperty() extends StaticAnnotation with java.lang.annotation.Annotation {
    override def annotationType(): Class[_ <: Annotation] = classOf[MetadataProperty]
}

class MetadataProvider() {
    private var metadata = Map[String, Any]()

    private def tryGetMember(name : String) : Option[java.lang.reflect.Method] = {
        val maybeMethod = this.getClass.getDeclaredMethod(name)
        if (maybeMethod != null) {
            val maybeAnnotation = maybeMethod.getAnnotation(classOf[MetadataProperty])
            if (maybeAnnotation != null) {
                return Some(maybeMethod)
            }
        }
        None
    }

    final def apply(name : String) = {
        if (metadata.contains(name)) {
            metadata(name)
        }
        val tryGetMethod = tryGetMember(name)

        if (tryGetMethod.isDefined) {
            tryGetMethod.get.invoke(this)
        }  else {
            null
        }
    }

    final def remove(name : String) = {
        val tryGetMethod = tryGetMember(name)
        if (tryGetMethod.isEmpty) {
            metadata -= name
        } else {
            //putting null in the metadata dictionary effectively removes a static member
            metadata += name -> null
        }
    }

    final def update(name : String, value : Any) = metadata += name -> value

    final def insertAll(seq : (String, Any) *) = metadata ++= seq
}
