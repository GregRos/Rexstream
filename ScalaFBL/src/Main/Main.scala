package Main

import fbl._

/**
  * Created by GregRos on 06/02/2016.
  */
object Main {

    def main(arr: Array[String]): Unit = {
        val list = ListVar[Int]
        val mapped = list.map(_.convert(n => n.asInstanceOf[Long], (n : Long) => n.asInstanceOf[Int]))
        list.value += 0 += 1 += 2
        list.value(0) = -1
        list.value.remove(1)
        mapped.value += 3 += 4 += 5
        mapped.value.remove(1)
        val dfg = 54

        val filtered = mapped.filter(_.convert(x => x % 2 == 0))

        println(list.value.toList)
        println(mapped.value.toList)
        println(filtered.value.toList)

        println(filtered.sum)
        println(mapped.sum)
        filtered.value += 5 += 10
        println(list.value.toList)
        println(mapped.value.toList)
        println(filtered.value.toList)
        filtered.value.remove(0)
        filtered.value.insert(1, 0, 5, 3)
        filtered.value(0) = 11
        filtered.value += 10 += 11 += 12
        filtered.value.remove(0)
        println(list.value.toList)
        println(mapped.value.toList)
        println(filtered.value.toList)

        list.value += 1 += 3

        println(filtered.sum)
        println(mapped.sum)


    }
}
