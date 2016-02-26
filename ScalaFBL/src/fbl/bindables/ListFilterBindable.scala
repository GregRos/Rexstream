package fbl.bindables

import fbl._
import fbl.bindables.collections.{FilterList, MapList}

/**
  * Created by GregRos on 26/02/2016.
  */
class ListFilterBindable[T](override val parent : CollectionBindable[T], map : BindableMap[T, Boolean])
    extends BaseCollectionBindable[T](new FilterList[T](parent.bindingPoints, map))
{

}

