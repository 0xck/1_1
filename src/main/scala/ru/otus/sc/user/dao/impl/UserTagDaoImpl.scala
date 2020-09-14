package ru.otus.sc.user.dao.impl

import ru.otus.sc.user.dao.UserTagDao
import ru.otus.sc.user.model.User.UniqueUserId
import ru.otus.sc.user.model.UserTag
import ru.otus.sc.user.model.UserTag.UserTagId

class UserTagDaoImpl extends UserTagDao {

  private var tags: Map[UserTagId, UserTag] = Map.empty
  /** tag -> users mapping, due to tag does not keep associated users itself */
  private var taggedUsers: Map[UserTagId, Set[UniqueUserId]] = Map.empty
  /** emulate pk for tags */
  private var id: Long = 1

  override def createTag(tag: UserTag): UserTag = {
    val newUserTag = tag.copy(id = Some(id))
    tags += (id        -> newUserTag)
    taggedUsers += (id -> Set.empty)
    id += 1
    newUserTag
  }

  override def getTag(tagId: UserTagId): Option[UserTag] = tags.get(tagId)

  override def updateTag(tag: UserTag): Option[UserTag] =
    for {
      tagId <- tag.id
      _     <- tags.get(tagId)
    } yield {
      tags += (tagId -> tag)
      tag
    }

  override def deleteTag(tagId: UserTagId): Option[UserTag] =
    tags.get(tagId) match {
      case deletedTag @ Some(_) =>
        tags -= tagId
        deletedTag
      case None => None
    }

  override def findByName(tagName: String): Seq[UserTag] =
    tags.values.filter(_.tagName == tagName).toVector

  override def getUsersByTag(tagId: UserTagId): Seq[UniqueUserId] =
    taggedUsers.getOrElse(tagId, Set[UniqueUserId]()).toVector

  override def getAllTags: Seq[UserTag] = tags.values.toVector

  override def tagUser(
      tagId: UserTagId,
      uniqueUserId: UniqueUserId
  ): Option[(UserTagId, UniqueUserId)] =
    taggedUsers.get(tagId) match {
      case Some(users) =>
        taggedUsers += (tagId -> (users + uniqueUserId))
        Some((tagId, uniqueUserId))
      case None => None
    }

  override def untagUser(
      tagId: UserTagId,
      uniqueUserId: UniqueUserId
  ): Option[(UserTagId, UniqueUserId)] =
    taggedUsers.get(tagId) match {
      case Some(users) =>
        taggedUsers += (tagId -> (users - uniqueUserId))
        Some((tagId, uniqueUserId))
      case None => None
    }
}
