package fbl.bindables

import fbl.bindables.collections.MapList
import fbl.{BindableMap, CollectionBindable}

/**
  * Created by GregRos on 26/02/2016.
  */
class ListMapBindable[TIn, TOut](override val parent : CollectionBindable[TIn], map : BindableMap[TIn, TOut])
    extends BaseCollectionBindable[TOut](new MapList[TIn, TOut](parent.bindingPoints, map))
{

}
