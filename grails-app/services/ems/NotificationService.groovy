package ems

import grails.gorm.services.Service

@Service(Notification)
interface NotificationService {

    Notification get(Serializable id)

    List<Notification> list(Map args)

    Long count()

    void delete(Serializable id)

    Notification save(Notification notification)

}