package fbl.bindables

import fbl.bindables.collections.MapList
import fbl.{BindableMap, ListBindable}

/**
  * Created by GregRos on 26/02/2016.
  */
class ListMapBindable[TIn, TOut](override val parent : ListBindable[TIn], map : BindableMap[TIn, TOut])
    extends BaseListBindable[TOut](new MapList[TIn, TOut](parent.bindingPoints, map))
{

}
