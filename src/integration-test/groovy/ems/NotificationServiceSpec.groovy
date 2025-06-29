package ems

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class NotificationServiceSpec extends Specification {

    NotificationService notificationService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Notification(...).save(flush: true, failOnError: true)
        //new Notification(...).save(flush: true, failOnError: true)
        //Notification notification = new Notification(...).save(flush: true, failOnError: true)
        //new Notification(...).save(flush: true, failOnError: true)
        //new Notification(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //notification.id
    }

    void "test get"() {
        setupData()

        expect:
        notificationService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Notification> notificationList = notificationService.list(max: 2, offset: 2)

        then:
        notificationList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        notificationService.count() == 5
    }

    void "test delete"() {
        Long notificationId = setupData()

        expect:
        notificationService.count() == 5

        when:
        notificationService.delete(notificationId)
        sessionFactory.currentSession.flush()

        then:
        notificationService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Notification notification = new Notification()
        notificationService.save(notification)

        then:
        notification.id != null
    }
}
