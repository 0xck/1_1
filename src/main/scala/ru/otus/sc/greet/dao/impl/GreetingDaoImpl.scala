package ru.otus.sc.greet.dao.impl

import ru.otus.sc.greet.dao.GreetingDao

class GreetingDaoImpl extends GreetingDao {
  def greetingPrefix: String  = "Hi"
  def greetingPostfix: String = "!"
}
