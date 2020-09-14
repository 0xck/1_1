package ru.otus.sc.countdown.dao

import ru.otus.sc.countdown.model.Countdown
import ru.otus.sc.countdown.model.Countdown.{CompareValues, CountdownId, CountdownValue, UpdaterId}

trait CountdownDao {
  def createCountdown(countdown: Countdown): Countdown
  def deleteCountdown(id: CountdownId): Option[Countdown]
  def getCountdown(id: CountdownId): Option[Countdown]
  def updateCountdown(countdown: Countdown): Option[Countdown]
  def findCountdownsByValue(value: CountdownValue, predicate: CompareValues): Seq[Countdown]
  def findCountdownsByUpdater(updater: UpdaterId): Seq[Countdown]
  def findAllDone: Seq[Countdown]
  def findAllNonDone: Seq[Countdown]
  def getAll: Seq[Countdown]
}
