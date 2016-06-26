import rexstream.operations.{Conversion, Operator}
import rexstream.rex.scalar.{RexConvert, RexMember, RexScalarLink, RexVar}
import rexstream.rex.vector._

package object rexstream {
    type RexTransform[TIn, TOut] = RexScalar[TIn] => RexScalar[TOut]

    import rexstream.rex._
    implicit class RexScalarExtensions[T](inner : RexScalar[T]) {

        def convert_>[TOut](conversion : RexScalar[Conversion[T, TOut]]) ={
            new RexConvert[T, TOut](inner, conversion) : RexScalar[TOut]
        }

        def convert_>[TOut](convertOut : T => TOut = null, convertIn : TOut => T = null) =
            new RexConvert[T, TOut](inner, (
                new Conversion[T, TOut] {
                    override val in = Option.apply(convertIn)
                    override val out = Option.apply(convertOut)
                } : Conversion[T, TOut]).const_>) : RexScalar[TOut]

        def link_> = {
            new RexScalarLink[T](inner) : RexScalar[T]
        }

        def member_>[TOut](memberName : RexScalar[String]) = new RexMember[T, TOut](inner, memberName)

    }

    implicit class RexVectorExtensions[T](inner : RexVector[T]) {
        def map_>[TOut](convert : RexTransform[T, TOut]) = {
            new RexMap[T, TOut](inner, convert) : RexVector[TOut]
        }

        def filter_>(f : RexTransform[T, Boolean]) = {
            new RexFilter[T](inner, f).link_> : RexVector[T]
        }

        def aggregate_>[TOut](f : RexScalar[Operator[T, TOut]]) : RexScalar[TOut] = {
            new RexAggregate[T, TOut](inner, f)
        }

        def sumBy_>[TNum](f : RexTransform[T, TNum])(implicit num : Numeric[TNum]) = {
            inner.map_>(f).sum_>
        }

        def link_> = {
            new RexVectorLink[T](inner) : RexVector[T]
        }

        def sort_>(orderingProvider : RexScalar[Ordering[T]]) : RexVector[T] ={
            new RexSort[T](inner, orderingProvider).link_>
        }

        def sort_>(implicit ordering : Ordering[T]) : RexVector[T] ={
            inner.sort_>(ordering.const_>)
        }
    }

    implicit class RexCreatorExtensions[T](inner : T) {
        def var_> = Var(inner)
        def const_> = Const(inner)
    }

    implicit class NumericRexExtensions[T](inner : RexVector[T])(implicit num : Numeric[T]) {
        def sum_> = {
            inner.aggregate_>((new Operator[T, T] {
                override def zero = num.zero
                override val isOrderInvariant = true
                override def plus(cur : T, other : T) = num.plus(cur, other)
                override def minus(cur : T, other : T) = num.minus(cur, other)
                override def equal(cur : T, other : T) = cur == other
            } : Operator[T, T]).const_>)
        }
    }

    def Var[T](v : T) = new RexVar[T](v) : RexScalar[T]

    def Const[T](v : T) = new RexVar[T](v, canWrite = false) : RexScalar[T]

    def ListVar[T] = new RexList[T]()
}
