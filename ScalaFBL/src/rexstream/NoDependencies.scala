package rexstream

/**
  * Created by GregRos on 04/06/2016.
  */
object NoDependencies extends DependencyProvider {
    def byName(name : String) = None

    def iterator = Iterator.empty
}
