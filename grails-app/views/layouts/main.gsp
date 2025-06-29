<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
    <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <asset:stylesheet src="application.css"/>
    <g:layoutHead/>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/">EMS System</a>

        <div class="collapse navbar-collapse">
            <ul class="navbar-nav mr-auto">
            <!-- Direct navigation links for ROLE_ADMIN -->
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <li class="nav-item">
                        <g:link controller="employee" action="index" class="nav-link">
                            <i class="fas fa-users"></i> Employees
                        </g:link>
                    </li>
                    <li class="nav-item">
                        <g:link controller="branch" action="index" class="nav-link">
                            <i class="fas fa-building"></i> Branches
                        </g:link>
                    </li>
                </sec:ifAllGranted>
            </ul>

            <sec:ifLoggedIn>
                <div class="navbar-text mr-3">
                    Welcome, <sec:username/>!
                    <sec:ifAllGranted roles="ROLE_ADMIN">
                        <span class="badge badge-danger ml-1">Admin</span>
                    </sec:ifAllGranted>
                </div>
                <form action="${createLink(controller: 'logout')}" method="POST" style="margin: 0;">
                    <input type="submit" class="btn btn-outline-light" value="Logout"/>
                </form>
            </sec:ifLoggedIn>
        </div>
    </div>
</nav>

<g:layoutBody/>

<div class="footer" role="contentinfo">
    <!-- Your footer content -->
</div>

<asset:javascript src="application.js"/>

<!-- Bootstrap JavaScript for dropdown functionality -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>