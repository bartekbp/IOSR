<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <script type="text/javascript" src="/webjars/jquery/2.0.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/gauge.min.js"></script>
    <title>Monitoring</title>

    <script type="text/javascript">

        //        function updateLogs() {
        //            $.ajax({url: 'poll_monitoring'})
        //                    .fail(function (jqXHR, textStatus, errorThrown) {
        //                        clearInterval(id);
        //                        $('#main_table').html("<tr><td>Error</td><td>No connection to server</td><td>-</td></tr>");
        //                    })
        //                    .done(function (response, textStatus, jqXHR) {
        //                        if (response != "") {
        //                            $('#main_table').html(response);
        //                        }
        //                    });
        //        }
        //        var id = setInterval(updateLogs, 500);
    </script>


    <style type="text/css">
        .table_row {
            line-height: 103px !important;
        }

        .image_holder {
            position: relative;
        }

        .up10px {
            margin-top: -10px;
        }

        .offline_info {
            line-height: 15px;
            position: absolute;
            top: 78px;
            left: 0;
            font-style: italic;
        }
    </style>
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
                <li class="active"><a href="/monitoring">Monitoring</a></li>
                <li><a href="/log_stream">Log stream</a></li>
                <li><a href="/statistics">Statistics</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/logout">Log out</a></li>
            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
</div>

<div style="margin: 70px 10px 10px;">
    <table class="table table-striped table-hover">
        <thead>
        <tr>
            <th>Status</th>
            <th>Node</th>
            <th>Last minute</th>
            <th>Last hour</th>
            <th>Last day</th>
            <th>Overall</th>
        </tr>
        </thead>
        <tbody id="main_table">
        <tr>
            <td class="table_row">
                <div class="image_holder">
                    <img class="up10px" src="${pageContext.request.contextPath}/images/status_ok.png"/>
                </div>
            </td>

            <td>
                <h3>kafka-node1</h3>
                <h4>10.220.150.1</h4>
            </td>

            <td>
                <div style="text-align: center;">
                    <p style="margin-bottom: 10px;">1023 logs</p>
                    <canvas id="foo1" height="40" width="60"></canvas>
                    <p style="margin-top: -7px;">17 logs/s</p>
                </div>
            </td>

            <td>
                <div>
                    <canvas id="foo2" height="40" width="80"></canvas>
                </div>
            </td>

            <td>
                <div>
                    <canvas id="foo3" height="40" width="80"></canvas>
                </div>
            </td>

            <td>
                <div>
                    <canvas id="foo4" height="40" width="80"></canvas>
                </div>
            </td>
        </tr>

        <tr>
            <td class="table_row">
                <div class="image_holder">
                    <img class="up10px" src="${pageContext.request.contextPath}/images/status_error.png"/>
                    <span class="offline_info">Offline: 10 s</span>
                </div>
            </td>

            <td>
                <h3>kafka-node1</h3>
                <h4>10.220.150.1</h4>
            </td>

            <td>
                hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
            </td>
        </tr>
        <%--Monitoring goes here--%>
        </tbody>
    </table>
</div>


<script type="text/javascript">
    var opts = {
        lines: 12, // The number of lines to draw
        angle: 0.1, // The length of each line
        lineWidth: 0.33, // The line thickness
        pointer: {
            length: 0.79, // The radius of the inner circle
            strokeWidth: 0.057, // The rotation offset
            color: '#000000' // Fill color
        },
        limitMax: true,   // If true, the pointer will not go past the end of the gauge
        colorStart: '#6FADCF',   // Colors
        colorStop: '#8FC0DA',    // just experiment with them
        strokeColor: '#E0E0E0',   // to see which ones work best for you
        generateGradient: true
    };
    var target = document.getElementById('foo1'); // your canvas element
    var gauge = new Gauge(target).setOptions(opts); // create sexy gauge!
    gauge.maxValue = 3000; // set max gauge value
    gauge.animationSpeed = 1; // set animation speed (32 is default value)
    gauge.set(1000); // set actual value
    console.log(gauge);
    target = document.getElementById('foo2'); // your canvas element
    gauge = new Gauge(target).setOptions(opts); // create sexy gauge!
    gauge.maxValue = 3000; // set max gauge value
    gauge.animationSpeed = 1; // set animation speed (32 is default value)
    gauge.set(1000); // set actual value
    console.log(gauge);
    target = document.getElementById('foo3'); // your canvas element
    gauge = new Gauge(target).setOptions(opts); // create sexy gauge!
    gauge.maxValue = 3000; // set max gauge value
    gauge.animationSpeed = 1; // set animation speed (32 is default value)
    gauge.set(1000); // set actual value
    console.log(gauge);
    target = document.getElementById('foo4'); // your canvas element
    gauge = new Gauge(target).setOptions(opts); // create sexy gauge!
    gauge.maxValue = 3000; // set max gauge value
    gauge.animationSpeed = 1; // set animation speed (32 is default value)
    gauge.set(1000); // set actual value
    console.log(gauge);
</script>

</body>

</html>