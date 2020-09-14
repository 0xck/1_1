package ru.otus.sc.countdown.dao.impl

import java.util.UUID

import ru.otus.sc.countdown.dao.CountdownDao
import ru.otus.sc.countdown.model.Countdown
import ru.otus.sc.countdown.model.Countdown.{CompareValues, CountdownId, CountdownValue, UpdaterId}

class CountdownDaoImpl extends CountdownDao {
  private var countdowns = Map[CountdownId, Countdown]()

  def createCountdown(countdown: Countdown): Countdown = {

    val id = UUID.randomUUID()
    val newCountdown = countdown match {
      case Countdown.Done(_)                 => Countdown.Done(Some(id))
      case Countdown.Tick(_, updater, value) => Countdown.Tick(Some(id), updater, value)
    }
    countdowns += (id -> newCountdown)
    newCountdown
  }

  def deleteCountdown(id: CountdownId): Option[Countdown] =
    for {
      deletedCountdown <- countdowns.get(id)
    } yield {
      countdowns -= id
      deletedCountdown
    }

  def getCountdown(id: CountdownId): Option[Countdown] = countdowns.get(id)

  def updateCountdown(countdown: Countdown): Option[Countdown] = {

    /** only Countdown.Tick can be updated */
    val (countdownId, updaterId) = countdown match {
      case Countdown.Tick(id, updater, _) => (id, Some(updater))
      case Countdown.Done(_)              => (None, None)
    }

    for {
      id               <- countdownId
      updater          <- updaterId
      currentCountdown <- countdowns.get(id)
    } yield {
      val newCountdown = currentCountdown match {
        case alreadyDone @ Countdown.Done(_) => alreadyDone
        case Countdown.Tick(id, _, value) =>
          val newValue = value - 1L
          if (newValue <= 0) Countdown.Done(id)
          else Countdown.Tick(id, updater, newValue)
      }
      countdowns += (id -> newCountdown)
      newCountdown
    }
  }

  def findCountdownsByValue(value: CountdownValue, predicate: CompareValues): Seq[Countdown] =
    countdowns.values.collect {
      case x @ Countdown.Tick(_, _, currentValue) if predicate(value, currentValue) => x
    }.toVector

  def findCountdownsByUpdater(updater: UpdaterId): Seq[Countdown] =
    countdowns.values.collect {
      case x @ Countdown.Tick(_, updaterId, _) if updaterId == updater => x
    }.toVector

  def findAllDone: Seq[Countdown] =
    countdowns.values.collect { case x @ Countdown.Done(_) => x }.toVector

  def findAllNonDone: Seq[Countdown] =
    countdowns.values.collect { case x @ Countdown.Tick(_, _, _) => x }.toVector

  def getAll: Seq[Countdown] = countdowns.values.toVector
}
