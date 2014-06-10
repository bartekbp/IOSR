<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.Map" %>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" />
    <script type="text/javascript" src="/webjars/jquery/2.0.3/jquery.min.js"></script>
    <script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>
    <script>
        $(function() {
            $( "#from_datepicker" ).datepicker();
            $( "#to_datepicker" ).datepicker();
            $( "#from_hour").timepicker();
            $( "#to_hour").timepicker();
        });
    </script>
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
<div style="margin: 70px 10px 10px;">
    <form action="statistics" method="GET">
        <div>
            <p>Please select time range</p>


        </div>
        <div>
            <span>Start date</span>
            <input type="text" id="from_datepicker" name="from" />
            <input type="text" id="from_hour" name="from_hour" />
            <span>End date</span>
            <input type="text" id="to_datepicker" name="to" />
            <input type="text" id="to_hour" name="to_hour" />
            <input type="submit" name="submit" value="go" />
        </div>
    </form>
</div>

<br/><br/>

<% if(request.getAttribute("report")!=null) {%>
<% pl.edu.agh.kaflog.master.statistics.Report report = (pl.edu.agh.kaflog.master.statistics.Report)request.getAttribute("report"); %>
<table width="800" margin-left="200">
    <tr>
        <th>HOST</th>
        <% for(pl.edu.agh.kaflog.master.statistics.Pair<String, Long> pair: report.getSeverityData()) {%>
        <th><%= pair.getFirst() %></th>
        <% }%>
        <th>ALL</th>
    </tr>
    <tr>
        <td>Cluster</td>
        <% for(pl.edu.agh.kaflog.master.statistics.Pair<String, Long> pair: report.getSeverityData()) {%>
        <td><%= pair.getSecond() %></td>
        <% }%>
        <td><%= report.getAll() %></td>
    </tr>

    <%
        long all;
        for(java.util.Map.Entry<String, java.util.Map<String, Long>> entry: report.getHostSeverityData().entrySet()) { %>
    <tr>
        <td><%= entry.getKey() %></td>
        <%
            all = 0;
            for(pl.edu.agh.kaflog.master.statistics.Pair<String, Long> pair: report.getSeverityData()) { %>
        <td><%= entry.getValue().get(pair.getFirst()) %></td>
        <%all += entry.getValue().get(pair.getFirst());%>
        <% } %>
        <td><%=all%></td>
    </tr>
    <% } %>

</table>
<%}%>

</body>

</html>