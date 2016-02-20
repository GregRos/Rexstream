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

        def map[TOut](convert : BindableMap[T, TOut]) = {
            new MapListBindable[T, TOut](inner, convert) : ListBindable[TOut]
        }

        def filter(f : BindableMap[T, Boolean]) = {
            new FilterListBindable[T](inner, f) : ListBindable[T]
        }

        def aggregate[TOut](f : Operator[T, TOut]) : Bindable[TOut] = {
            new ListAggregateBindable[T, TOut](inner, f)
        }

        def sumBy[TNum](f : BindableMap[T, TNum])(implicit num : Numeric[TNum]) = {
            inner.map(f).sum
        }

        def linkList = {
            inner.map(b => b.link)
        }
    }

    implicit class NumericListBindableExt[T](inner : ListBindable[T])(implicit num : Numeric[T]) {
        def sum = {
            inner.aggregate(new Operator[T, T] {
                override def zero = num.zero
                override val isOrderInvariant = true
                override def plus(cur : T, other : T) = num.plus(cur, other)
                override def minus(cur : T, other : T) = num.minus(cur, other)
                override def equal(cur : T, other : T) = cur == other
            })
        }
    }

    def Var[T](v : T) = new VarBindable[T](v)

    def ComputeVar[T](f : Unit => T) = new ComputedBindable[T](f)

    def Const[T](v : T) = new ConstBindable[T](v)

    def ListVar[T] = new SimpleListBindable[T]()
}
