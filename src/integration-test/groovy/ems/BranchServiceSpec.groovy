package ems

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BranchServiceSpec extends Specification {

    BranchService branchService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Branch(...).save(flush: true, failOnError: true)
        //new Branch(...).save(flush: true, failOnError: true)
        //Branch branch = new Branch(...).save(flush: true, failOnError: true)
        //new Branch(...).save(flush: true, failOnError: true)
        //new Branch(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //branch.id
    }

    void "test get"() {
        setupData()

        expect:
        branchService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Branch> branchList = branchService.list(max: 2, offset: 2)

        then:
        branchList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        branchService.count() == 5
    }

    void "test delete"() {
        Long branchId = setupData()

        expect:
        branchService.count() == 5

        when:
        branchService.delete(branchId)
        sessionFactory.currentSession.flush()

        then:
        branchService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Branch branch = new Branch()
        branchService.save(branch)

        then:
        branch.id != null
    }
}
