<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <script type="text/javascript" src="/webjars/jquery/2.0.3/jquery.min.js"></script>
    <title>Statistics</title>
</head>

<body>

    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/monitoring">Kaflog</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a href="/monitoring">Monitoring</a></li>
                    <li><a href="/log_stream">Log stream</a></li>
                    <li class="active"><a href="/statistics">Statistics</a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="/logout">Log out</a></li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </div>

    <br>
    Message: ${message}

    <table class="table table-striped table-hover">
        <caption class="text-left">Clients</caption>
        <thead>
        <th>#</th>
        <th>Addres</th>
        </thead>
        <tbody>
        <c:forEach items="${clients}" var="client" varStatus="status">
            <tr>
                <td>${status.index}</td>
                <td>${client}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</body>

</html>