package rexstream.rex

import rexstream.events._
import rexstream.{AnyRex, MetadataProvider}

import scala.collection._
/**
  * Created by GregRos on 04/06/2016.
  */
trait DefaultRex[TChange] extends AnyRex[TChange] {
    private lazy val _changeTokens = mutable.MutableList[AutoCloseable]()
    override final def addChangeDetector(detector : ChangeDetector[_ <: TChange]): Unit = {
        val s = detector.changed ++= changed
        _changeTokens += s
    }

    override final val metadata = MetadataProvider()
    private var _isClosed = false
    override final def isClosed = _isClosed

    protected val _closing = new Event[Unit]

    override final val changed = new Event[TChange]()
    override final val closing = _closing : AbsEvent[Unit]
    override def close(): Unit = {
        _closing.raise(())
        _changeTokens.foreach(x => x.close())
        changed.close()
        _isClosed = true
    }
}
