package fbl.bindables

import fbl._
import fbl.bindables.collections.{SimpleList, FilterList}

/**
  * Created by GregRos on 26/02/2016.
  */
class SimpleBindable[T]()
    extends BaseListBindable[T](new SimpleList[T](x => new VarBindable[T]()))
{
    override def parent: AnyBindable = null
}
