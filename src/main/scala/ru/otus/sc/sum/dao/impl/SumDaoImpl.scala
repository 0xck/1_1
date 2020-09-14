package ru.otus.sc.sum.dao.impl

import ru.otus.sc.sum.dao.SumDao

import scala.annotation.tailrec

class SumDaoImpl extends SumDao {
  def sum(a: Long, b: Long, external: Boolean): String = {
    lazy val externalDelay = fib(10000000000L)
    val result = {
      if (!external) a + b
      else {
        externalDelay + 1
        a + b
      }
    }

    result.toString
  }

  /** Can not use Thread.sleep, so emulate delay with fibonacci 10000000000 calculation */
  private def fib(n: Long): Long = {
    @tailrec
    def fib_tail(n: Long, a: Long, b: Long): Long = {
      if (n == 0) a
      else fib_tail(n - 1L, b, a + b)
    }

    fib_tail(n, 0L, 1L)
  }
}
