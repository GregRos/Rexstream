package rexstream.rex

import rexstream.events._
import rexstream.{AnyRex, CommonMetadata, StandardRexInfo}
/**
  * Created by GregRos on 04/06/2016.
  */
trait StandardRexImplementation extends AnyRex {
    override final type MyMetadata = CommonMetadata
    override final type MyInfo = StandardRexInfo
    override final val metadata = new CommonMetadata()

    private var _isClosed = false
    override final def isClosed = _isClosed

    override final val changed = new Event[MyChangeInfo]()

    override def close(): Unit = {
        changed.close()
        _isClosed = true
    }

}
