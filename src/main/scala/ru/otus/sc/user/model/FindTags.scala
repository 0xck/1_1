package ru.otus.sc.user.model

import ru.otus.sc.user.model.User.UniqueUserId
import ru.otus.sc.user.model.UserTag.UserTagId

sealed trait FindUserTagsRequest
object FindUserTagsRequest {
  case class ByName(tagName: String)    extends FindUserTagsRequest
  case class UsersByTag(tag: UserTagId) extends FindUserTagsRequest
  case object GetAll                    extends FindUserTagsRequest
}

sealed trait FindUserTagsResponse
object FindUserTagsResponse {
  case class TagsResult(tags: Seq[UserTag])        extends FindUserTagsResponse
  case class UsersResult(users: Seq[UniqueUserId]) extends FindUserTagsResponse
}
