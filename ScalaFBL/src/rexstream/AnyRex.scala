package rexstream

import rexstream.rex._
import rexstream.events._
import scala.collection._

trait AnyRex[TChange] extends AutoCloseable {
    def depends : DependencyProvider
    def metadata : MetadataProvider
    def info : RexInfo

    def isClosed : Boolean
    def changed : Event[TChange]
    def closing: AbsEvent[Unit]

    protected final def makeSureNotClosed(): Unit = {
        if (isClosed) {
            throw Errors.Object_closed(this, "This Bindable object has been closed.")
        }
    }

    protected[rexstream] def consistencyCheck()
}


