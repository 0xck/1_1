package ru.otus.sc.user.model

import ru.otus.sc.user.model.UserTag.UserTagId

sealed trait FindUsersRequest
object FindUsersRequest {
  case class ByFirstName(firstName: String) extends FindUsersRequest
  case class ByLastName(lastName: String)   extends FindUsersRequest
  case class ByTag(tag: UserTagId)          extends FindUsersRequest
  case object GetAll                        extends FindUsersRequest
}

sealed trait FindUsersResponse
object FindUsersResponse {
  case class Result(users: Seq[User]) extends FindUsersResponse
}
