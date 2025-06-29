<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>
<div id="content" role="main">
    <div class="container">
        <section class="row">
            <a href="#edit-employee" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
            <div class="nav" role="navigation">
                <ul>
                    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </ul>
            </div>
        </section>
        <section class="row">
            <div id="edit-employee" class="col-12 content scaffold-edit" role="main">
                <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
                <g:if test="${flash.message}">
                    <div class="message" role="status">${flash.message}</div>
                </g:if>
                <g:hasErrors bean="${this.employee}">
                    <ul class="errors" role="alert">
                        <g:eachError bean="${this.employee}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </g:hasErrors>
                <g:form resource="${this.employee}" method="PUT">
                    <g:hiddenField name="version" value="${this.employee?.version}" />
                    <fieldset class="form">

                        <div class="form-group">
                            <label for="firstName">First Name</label>
                            <g:textField name="firstName" class="form-control" value="${this.employee?.firstName}" required=""/>
                        </div>

                        <div class="form-group">
                            <label for="lastName">Last Name</label>
                            <g:textField name="lastName" class="form-control" value="${this.employee?.lastName}" required=""/>
                        </div>

                        <div class="form-group">
                            <label for="email">Email</label>
                            <g:textField name="email" type="email" class="form-control" value="${this.employee?.email}" required=""/>
                        </div>

                        <div class="form-group">
                            <label for="phone">Phone</label>
                            <g:textField name="phone" class="form-control" value="${this.employee?.phone}"/>
                        </div>

                        <div class="form-group">
                            <label for="username">Username</label>
                            <g:textField name="username" class="form-control" value="${currentUser?.username}" required=""/>
                            <small class="form-text text-muted">Current username will be updated</small>
                        </div>

                        <div class="form-group">
                            <label for="password">New Password</label>
                            <g:passwordField name="password" class="form-control" placeholder="Leave blank to keep current password"/>
                            <small class="form-text text-muted">Leave blank if you don't want to change the password</small>
                        </div>

                        <div class="form-group">
                            <label for="branch">Branch</label>
                            <g:select name="branch.id"
                                      from="${ems.Branch.list()}"
                                      optionKey="id"
                                      optionValue="name"
                                      value="${this.employee?.branch?.id}"
                                      class="form-control" required=""/>
                        </div>

                        <div class="form-group">
                            <label for="role">Role</label>
                            <g:select name="role.id"
                                      from="${roles}"
                                      optionKey="id"
                                      optionValue="authority"
                                      value="${this.employee?.role?.id}"
                                      class="form-control" required=""/>
                        </div>

                        <div class="form-group">
                            <label for="supervisor">Supervisor</label>
                            <g:select name="supervisor.id"
                                      from="${supervisorList}"
                                      optionKey="id"
                                      optionValue="${{it.firstName + ' ' + it.lastName + ' (' + it.role?.authority + ')'}}"
                                      value="${this.employee?.supervisor?.id}"
                                      noSelection="['': 'No Supervisor']"
                                      class="form-control"/>
                        </div>

                    </fieldset>
                    <fieldset class="buttons">
                        <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                    </fieldset>
                </g:form>
            </div>
        </section>
    </div>
</div>
</body>
</html>