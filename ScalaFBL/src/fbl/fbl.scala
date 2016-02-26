package object fbl {
    type BindableMap[TIn, TOut] = ValueBindable[TIn] => ValueBindable[TOut]

    import fbl.bindables._
    implicit class BindableExt[T](inner : ValueBindable[T]) {
        def convert[TOut](convertOut : T => TOut = null, convertIn : TOut => T = null) =
            new ConvertBindable[T, TOut](inner, Option.apply(convertIn), Option.apply(convertOut)) : ValueBindable[TOut]

        def link = {
            new LinkBindable[T](inner) : ValueBindable[T]
        }
    }

    implicit class ListBindableExt[T](inner : ListBindable[T]) {

        def map_>[TOut](convert : BindableMap[T, TOut]) = {
            new ListMapBindable[T, TOut](inner, convert) : ListBindable[TOut]
        }

        def filter_>(f : BindableMap[T, Boolean]) = {
            new ListFilterBindable[T](inner, f) : ListBindable[T]
        }

        def aggregate_>[TOut](f : Operator[T, TOut]) : ValueBindable[TOut] = {
            new ListAggregateBindable[T, TOut](inner, f)
        }

        def sumBy_>[TNum](f : BindableMap[T, TNum])(implicit num : Numeric[TNum]) = {
            inner.map_>(f).sum
        }

        def link_> = {
            inner.map_>((b : ValueBindable[T]) => b.link)
        }
    }

    implicit class NumericListBindableExt[T](inner : ListBindable[T])(implicit num : Numeric[T]) {
        def sum = {
            inner.aggregate_>(new Operator[T, T] {
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

    def ListVar[T] = new SimpleBindable[T]()
}
