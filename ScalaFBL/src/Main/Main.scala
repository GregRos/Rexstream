package Main

import rexstream._
import rexstream.debug._
import scala.reflect.runtime.universe._
import rexstream.util._

class Example {
    def prop = 0
    def prop_=(x : Int) = {}
    val x = 5
    var y = 7
}
/**
  * Created by GregRos on 06/02/2016.
  */
object Main {

    def main(arr: Array[String]): Unit = {
        val str = "Hi"

        val s = str.var_>.member_>("length")

        val var_1 = 4.var_>
        var var_2 = 5.var_>
        var_1.binding = var_2.asBinding()

        var_1.value = 5
        var_2.value = 6
        println(s"$var_1 == $var_2")

        println(s)
        val list = ListVar_>[Int]
        val mapped = list.map_>(_.convert_>(n => n.asInstanceOf[Long], (n : Long) => n.asInstanceOf[Int]))
        list += 0 += 1 += 2
        list(0) = -1
        list.remove(1)
        mapped += 3 += 4 += 5
        mapped.remove(1)
        val dfg = 54

        val filtered = mapped.filter_>(_.convert_>(x => x % 2 == 0))
        println(list)
        println(mapped)
        println(filtered)

        println(filtered.sum)
        println(mapped.sum)
        filtered += 5 += 10
        println(list)
        println(mapped)
        println(filtered)
        filtered.remove(0)
        filtered.insert(1, 5, 3)
        filtered(0) = 11
        filtered += 10 += 11 += 12
        filtered.remove(0)
        val sorted = mapped.sort_>
        sorted.consistencyCheck()
        println(list)
        println(mapped)
        println(filtered)
        sorted.consistencyCheck()
        list += 1 += 3
        println(mapped)
        println(sorted)
        sorted.consistencyCheck()
        println(filtered.sum_>)
        println(mapped.sum_>)
        println(mapped)
        println(sorted)
        sorted += 1 += 2 += 3
        list(0) = 1000
        sorted.consistencyCheck()
    }
}
