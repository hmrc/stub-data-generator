package hmrc.smartstub

import org.scalacheck._
import Gen._
import java.time.LocalDate

trait Temporal extends Any {
  def date(start: LocalDate, end: LocalDate): Gen[LocalDate] = 
    choose(start.toEpochDay, end.toEpochDay).map(LocalDate.ofEpochDay)

  def date(
    start: Int = 1970,
    end: Int = 2000
  ): Gen[LocalDate] = date(
    LocalDate.of(start, 1, 1),
    LocalDate.of(end, 12, 31)
  )
}
