package rexstream

/**
  * Created by GregRos on 04/06/2016.
  */
class SourceAndProvider(@DependencyAnnotation val source : Any, @DependencyAnnotation val provider : Any) extends ReflectingDependencyProvider() {

}
