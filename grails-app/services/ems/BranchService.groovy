package ems

import grails.gorm.services.Service

@Service(Branch)
interface BranchService {

    Branch get(Serializable id)

    List<Branch> list(Map args)

    Long count()

    void delete(Serializable id)

    Branch save(Branch branch)

}