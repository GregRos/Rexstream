package rexstream

import rexstream.rex._
import rexstream.events._
import scala.collection._

trait AnyRex extends AutoCloseable  {
    protected object NaryDependency {
        val empty = new NaryDependency(Map.empty)
        def source(src : AnyRex) = new NaryDependency(Map.empty + ("source" -> src))
        def multiple(pairs : (String, AnyRex)*) = {
            new NaryDependency(Map.empty ++ pairs)
        }

        def sourceAndProvider(source : AnyRex, provider : AnyRex) = {
            multiple(
                "source" -> source,
                "provider" -> provider
            )
        }
    }

    protected def bindableName = this.getClass.getTypeName.replaceAll("Bindable$", "")

    protected def isFunctional = true

    final val metadata = new MetadataProvider()
    metadata.insertAll(
        "type" -> bindableName,
        "isFunctional" -> isFunctional
    )

    /**
      * The type used to indicate information about a change.
      */
    type ChangeInfo <: ContextualChangeInfo

    private var _isClosed = false
    /**
      * Returns whether this bindable has been closed/disconnected.
      *
      * @return
      */
    final def isClosed = _isClosed

    /**
      * Whether reading to this bindable is possible. This property may report false positives, but not false negatives.
      *
      * @return
      */
    def canRead : Boolean

    /**
      * Whether writing to this bindable is possible. This property may report false positives, but not false negatives.
      *
      * @return If this bindalbe
      */
    def canWrite : Boolean

    /**
      * An object that lets you inspect the dependencies of this bindable in a reflective manner.
      *
      * @return The dependency element.
      */
    def dependency : NaryDependency

    /**
      * Raised when this bindable is changed.
      */
    final val changed = new Event[ChangeInfo]()

    /**
      * Disconnects this bindable, causing it to clear its observers and stop observing dependencies.
      */
    def close(): Unit = {
        changed.close()
        _isClosed = true
    }

    /**
      * A helper method that checks this bindable hasn't been closed; if it has, throws an exception.
      */
    protected final def makeSureNotClosed(): Unit = {
        if (isClosed) {
            throw Errors.Object_closed(this, "This Bindable object has been closed.")
        }
    }

    /**
      * Validates this bindable's integrity. This is a debugging feature.
      *
      * @return
      */
    protected[rexstream] def consistencyCheck() : Unit
}

