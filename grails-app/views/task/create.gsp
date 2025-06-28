<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
    <div id="content" role="main">
        <div class="container">
            <section class="row">
                <a href="#create-task" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
                <div class="nav" role="navigation">
                    <ul>
                        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    </ul>
                </div>
            </section>
            <section class="row">
                <div id="create-task" class="col-12 content scaffold-create" role="main">
                    <h1><g:message code="default.create.label" args="[entityName]" /></h1>
                    <g:if test="${flash.message}">
                    <div class="message" role="status">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${this.task}">
                    <ul class="errors" role="alert">
                        <g:eachError bean="${this.task}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                        </g:eachError>
                    </ul>
                    </g:hasErrors>
                    <g:form resource="${this.task}" method="POST">
                        <fieldset class="form">
                            <div class="fieldcontain required">
                                <label for="title">Title</label>
                                <g:textField name="title" value="${task?.title}" required="true"/>
                            </div>

                            <div class="fieldcontain required">
                                <label for="description">Description</label>
                                <g:textArea name="description" value="${task?.description}" required="true"/>
                            </div>

                            <div class="fieldcontain required">
                                <label for="deadline">Deadline</label>
                                <input type="date" name="deadline" id="deadline" value="${params.deadline ?: ''}" required />
                            </div>

                            <div class="fieldcontain required">
                                <label for="assignedBy.id">Assigned By</label>
                                <g:select name="assignedBy.id"
                                          from="${employeeList}"
                                          optionKey="id"
                                          optionValue="${{ it.fullName + ' (' + it.position + ')' }}"
                                          noSelection="['':'-- Select Employee --']"
                                          required="true"/>
                            </div>

                            <div class="fieldcontain required">
                                <label for="assignedTo.id">Assigned To</label>
                                <g:select name="assignedTo.id"
                                          from="${employeeList}"
                                          optionKey="id"
                                          optionValue="${{ it.fullName + ' (' + it.position + ')' }}"
                                          noSelection="['':'-- Select Employee --']"
                                          required="true"/>
                            </div>

                        </fieldset>
                        <fieldset class="buttons">
                            <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                        </fieldset>
                    </g:form>
                </div>
            </section>
        </div>
    </div>
    </body>
</html>
