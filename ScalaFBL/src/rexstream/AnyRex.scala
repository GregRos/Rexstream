package rexstream

import rexstream.rex._
import rexstream.events._
import scala.collection._

trait AnyRex extends AutoCloseable {
    type MyChangeInfo <: ContextualChangeData
    def depends : DependencyProvider
    def metadata : MetadataProvider
    def info : RexInfo

    def isClosed : Boolean
    def changed : Event[MyChangeInfo]

    protected final def makeSureNotClosed(): Unit = {
        if (isClosed) {
            throw Errors.Object_closed(this, "This Bindable object has been closed.")
        }
    }

    protected[rexstream] def consistencyCheck()
}


