package ar.com.caeldev.bsacore.services

import ar.com.caeldev.bsacore.domain.Notification
import ar.com.caeldev.bsacore.validations.{ Operation, Rule, Validation }
import ar.com.caeldev.bsacore.validations.rules.{ GroupReceiversExists, GroupExists, NotNull, NotEmpty }
import ar.com.caeldev.bsacore.connectors.NotifyUsingConnectors
import org.slf4j.LoggerFactory

class NotificationService(implicit val mot: Manifest[Notification]) extends Service[Notification] with Validation[Notification] with NotifyUsingConnectors {

  def add(entity: Notification): Notification = {
    logger.info("Enter Add method")
    validate(entity, Operation.add)
    dao.save(entity)
    logger.info("Enter To notify")
    notify(entity)
    logger.info("retrieving Notification")
    dao.findById(entity.id)
  }

  def delete(id: Any) {
    logger.info(s"Enter Delete method id: $id")
    val entityToDelete: Notification = dao.findById(id)
    validate(entityToDelete, Operation.delete)
    catcher {
      dao.remove(entityToDelete)
    }
  }

  def update(entity: Notification): Notification = {
    logger.info("Enter Update method")
    throw new UnsupportedOperationException()
  }

  def get(id: Any): Notification = {
    logger.info(s"Enter Get method id: $id")
    dao.findById(id)
  }

  def applyRulesFor(entity: Notification, operation: Operation.Value): List[Rule[_]] = {
    NotificationRules.get(entity, operation)
  }
}

object NotificationRules {

  def get(entity: Notification, operation: Operation.Value): List[Rule[_]] = {
    operation match {
      case Operation.add => {
        List(
          new Rule[Long](entity.receivers_group_id, NotNull.get),
          new Rule[String](List(entity.message, entity.receivers_group_id.toString, entity.subject, entity.id.toString, entity.sender_id.toString), NotEmpty.get),
          new Rule[Long](entity.receivers_group_id, GroupExists.get),
          new Rule[Long](entity.receivers_group_id, GroupReceiversExists.get))
      }
      case Operation.update => {
        List.empty
      }
      case Operation.delete => {
        List.empty
      }
    }
  }

}
