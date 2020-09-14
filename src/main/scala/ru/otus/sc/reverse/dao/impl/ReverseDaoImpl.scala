package ru.otus.sc.reverse.dao.impl

import ru.otus.sc.reverse.dao.ReverseDao

class ReverseDaoImpl extends ReverseDao {
  def reverse(word: String): String = word.reverse
}
