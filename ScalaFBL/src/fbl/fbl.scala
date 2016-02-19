package object fbl {
    type BindableMap[TIn, TOut] = Bindable[TIn] => Bindable[TOut]

    import fbl.bindables._
    implicit class BindableExt[T](inner : Bindable[T]) {
        def convert[TOut](convertOut : T => TOut = null, convertIn : TOut => T = null) =
            new ConvertBindable[T, TOut](inner, Option.apply(convertIn), Option.apply(convertOut)) : Bindable[TOut]

        def link = {
            new LinkBindable[T](inner) : Bindable[T]
        }
    }

    implicit class ListBindableExt[T](inner : ListBindable[T]) {



        def mapBind[TOut](convert : BindableMap[T, TOut]) = {
            new MapBindable[T, TOut](inner, convert) : ListBindable[TOut]
        }

        def mapFunc[TOut](convertOut : T => TOut = null, convertIn : TOut => T = null) = {
            mapBind((b : Bindable[T]) => b.convert(convertOut, convertIn))
        }

        def filterBind(f : BindableMap[T, Boolean]) = {
            new FilterBindable[T](inner, f) : ListBindable[T]
        }

        def filterFunc(f : T => Boolean) = {
            filterBind((b : Bindable[T]) => b.convert(f))
        }

        def linkList = {
            inner.mapBind(x => x.link)
        }
    }

    def Var[T](v : T) = new VarBindable[T](v)

    def ComputeVar[T](f : Unit => T) = new ComputedBindable[T](f)

    def Const[T](v : T) = new ConstBindable[T](v)

    def ListVar[T] = new BufferBindable[T]()
}
