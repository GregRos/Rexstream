package fbl

import fbl.bindables._
import fbl.events._
import scala.collection._
trait AnyBindable {
    private var _isDisposed = false

    final def isDisposed = _isDisposed

    type ChangeInfo <: ContextualChangeInfo

    def canRead : Boolean

    def canWrite : Boolean

    def parent : AnyBindable

    final val changed = new Event[ChangeInfo]()

    def close(): Unit = {
        changed.close()
        _isDisposed = true
    }
}

