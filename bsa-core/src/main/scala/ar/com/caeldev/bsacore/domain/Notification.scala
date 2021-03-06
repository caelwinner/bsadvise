package ar.com.caeldev.bsacore.domain

import org.joda.time.DateTime

case class Notification(
    id: Long,
    sender_id: Long,
    receivers_group_id: Long,
    message: String,
    subject: String) {

  var sentAt: DateTime = _
  var status: String = Status.draft.toString
  var createdAt: DateTime = new DateTime()
}

object Status extends Enumeration {
  type Status = Value
  val draft = Value("draft")
  val sending = Value("sending")
  val sent = Value("sent")
}
