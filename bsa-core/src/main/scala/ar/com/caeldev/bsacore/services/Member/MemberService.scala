package ar.com.caeldev.bsacore.services.member

import ar.com.caeldev.bsacore.domain.{ Member }
import ar.com.caeldev.bsacore.services.common.Service
import ar.com.caeldev.bsacore.services.validations.Rule
import ar.com.caeldev.bsacore.services.common.rules.NotEmptyRule

class MemberService(implicit val mot: Manifest[Member]) extends Service[Member] {

  def add(entity: Member): Member = {
    validate(entity)
    dao.save(entity)
    dao.findById(entity.id)
  }

  def delete(id: Long) {
    val entityToDelete: Member = dao.findById(id)
    dao.remove(entityToDelete)
  }

  def update(entity: Member): Member = {
    validate(entity)
    val entityToDelete: Member = dao.findById(entity.id)
    dao.remove(entityToDelete)
    dao.save(entity)
    dao.findById(entity.id)
  }

  def get(id: Long): Member = {
    dao.findById(id)
  }

  def applyRulesUsing(entity: Member): List[Rule[_]] = {
    val rule = List(new Rule[String](List(entity.id.toString, entity.role_id.toString, entity.firstName, entity.lastName, entity.email), NotEmptyRule.rule))
    rule
  }
}
