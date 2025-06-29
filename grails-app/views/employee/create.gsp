<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <title>Create Employee</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div class="container">
    <h1>Create Employee</h1>

    <g:if test="${flash.message}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>

    <g:hasErrors bean="${employee}">
        <div class="alert alert-danger">
            <g:renderErrors bean="${employee}" as="list" />
        </div>
    </g:hasErrors>

    <g:form action="save" method="POST">
        <div class="form-group">
            <label>First Name</label>
            <g:textField name="firstName" class="form-control" value="${employee?.firstName}"/>
        </div>

        <div class="form-group">
            <label>Last Name</label>
            <g:textField name="lastName" class="form-control" value="${employee?.lastName}"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <g:textField name="email" class="form-control" value="${employee?.email}"/>
        </div>

        <div class="form-group">
            <label>Phone</label>
            <g:textField name="phone" class="form-control" value="${employee?.phone}"/>
        </div>

        <div class="form-group">
            <label>Username</label>
            <g:textField name="username" class="form-control" value="${employee?.username}" required=""/>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <g:passwordField name="password" id="password" class="form-control" required=""/>
        </div>

        <div class="form-group">
            <label>Branch</label>
            <g:select name="branch.id"
                      from="${ems.Branch.list()}"
                      optionKey="id"
                      optionValue="name"
                      class="form-control"/>
        </div>

        <div class="form-group">
            <label for="role">Role</label>
            <g:select name="role.id" from="${roles}"
                      optionKey="id" optionValue="authority"
                      class="form-control" id="roleSelect" required=""/>
        </div>

        <div class="form-group">
            <label>Supervisor</label>
            <g:select name="supervisor.id"
                      from="${supervisorList}"
                      optionKey="id"
                      optionValue="${{it.firstName + ' ' + it.lastName + ' (' + it.role?.authority + ')'}}"
                      noSelection="['': 'Select Supervisor (Optional)']"
                      class="form-control"/>
        </div>

        <div class="form-actions">
            <g:submitButton name="create" class="btn btn-primary" value="Create"/>
            <g:link class="btn btn-secondary" action="index">Cancel</g:link>
        </div>
    </g:form>
</div>

<script>
    $(document).ready(function() {});
</script>
</body>
</html>