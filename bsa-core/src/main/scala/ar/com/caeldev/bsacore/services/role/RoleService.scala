package ar.com.caeldev.bsacore.services.role

import ar.com.caeldev.bsacore.services.common.{ Operation, Service }
import ar.com.caeldev.bsacore.services.validations.{ Rule }
import ar.com.caeldev.bsacore.services.common.rules.NotEmpty
import scala.util.control.Exception._
import ar.com.caeldev.bsacore.domain.Role
import ar.com.caeldev.bsacore.services.exceptions.ServiceException

class RoleService(implicit val mot: Manifest[Role]) extends Service[Role] {

  def add(entity: Role): Role = {
    validate(entity, Operation.add.toString)
    dao.save(entity)
    dao.findById(entity.id)
  }

  def delete(id: Long) {
    val entityToDelete: Role = dao.findById(id)
    validate(entityToDelete, Operation.delete.toString)
    dao.remove(entityToDelete)
  }

  def update(entity: Role): Role = {
    validate(entity, Operation.update.toString)
    val entityToDelete: Role = dao.findById(entity.id)
    dao.remove(entityToDelete)
    dao.save(entity)
    dao.findById(entity.id)
  }

  def get(id: Long): Role = {
    dao.findById(id)
  }

  def applyRulesFor(entity: Role, operation: String): List[Rule[_]] = {
    RoleRules.get(entity, operation)
  }
}

object RoleRules {

  val operationCatch = catching(classOf[NoSuchElementException]).withApply(e => throw new ServiceException(e))

  def get(entity: Role, operation: String): List[Rule[_]] = {
    var results: List[Rule[_]] = Nil
    operationCatch {
      Operation.withName(operation) match {
        case Operation.add => {
          results = List(
            new Rule[String](List(entity.description, entity.id.toString), NotEmpty.get))
        }
        case Operation.update => {
          results = List(
            new Rule[String](List(entity.description, entity.id.toString), NotEmpty.get))
        }
        case Operation.delete => { results = List() }
      }
    }
    results
  }

}
