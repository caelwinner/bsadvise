package ar.com.caeldev.bsacore.services

import org.scalatest.{ GivenWhenThen, FunSpec }
import ar.com.caeldev.bsacore.domain.{ Member, Role, DomainSamples }
import ar.com.caeldev.bsacore.services.common.Service
import ar.com.caeldev.bsacore.services.role.RoleService
import ar.com.caeldev.bsacore.services.member.MemberService
import ar.com.caeldev.bsacore.services.exceptions.ServiceException

class MemberServiceSuite extends FunSpec with GivenWhenThen {

  describe("A Member Service") {
    it("Should add a new valid Member") {
      Given("a Role that exists and it was persisted")
      val role: Role = DomainSamples.roles(1000)
      val roleService: Service[Role] = new RoleService()
      roleService.add(role)

      And("a valid Member")
      val member: Member = DomainSamples.members(1001)

      When("try to persist the member")
      val memberService: Service[Member] = new MemberService()
      val persistedMember = memberService.add(member)

      Then("should pass successfully")
      assert(persistedMember.id === member.id)
      assert(persistedMember.email === member.email)
      assert(persistedMember.lastName === member.lastName)
      assert(persistedMember.firstName === member.firstName)
      assert(persistedMember.role_id === member.role_id)

      memberService.delete(persistedMember.id)
      roleService.delete(role.id)

      val memberFromDB: Member = memberService.get(persistedMember.id)
      assert(memberFromDB == null)

    }

    it("Should not add a new not valid Member") {
      Given("a not valid Member with an invalid role")
      val member: Member = DomainSamples.members(1011)

      When("try to persist the member")
      Then("should pass successfully")
      val memberService: Service[Member] = new MemberService()
      intercept[ServiceException] {
        memberService.add(member)
      }
    }

    it("Should update valid Member") {
      Given("a Role that exists and it was persisted")
      val role: Role = DomainSamples.roles(1000)
      val roleService: Service[Role] = new RoleService()
      roleService.add(role)

      And("a valid Member persisted")
      val member: Member = DomainSamples.members(1001)
      val memberService: Service[Member] = new MemberService()
      memberService.add(member)

      And("update the entity Member")
      val memberUpdate: Member = DomainSamples.members(1013)

      When("try to update the member to DB")
      val memberUpdated: Member = memberService.update(memberUpdate)

      Then("should pass successfully")
      assert(memberUpdated.firstName != member.firstName)
      memberService.delete(memberUpdated.id)
      val memberFromDB: Member = memberService.get(member.id)
      assert(memberFromDB == null)
    }

    it("Should delete valid Member") {
      Given("a Role that exists and it was persisted")
      val role: Role = DomainSamples.roles(1000)
      val roleService: Service[Role] = new RoleService()
      roleService.add(role)

      And("a valid Member persisted")
      val member: Member = DomainSamples.members(1001)
      val memberService: Service[Member] = new MemberService()
      memberService.add(member)

      When("try to delete the member to DB")
      memberService.delete(member.id)

      Then("should pass successfully")
      val memberFromDB: Member = memberService.get(member.id)
      assert(memberFromDB == null)
    }
  }
}
