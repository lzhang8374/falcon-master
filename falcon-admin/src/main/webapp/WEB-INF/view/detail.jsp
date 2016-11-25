<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>服务列表</title>
    <%@ include file="common/include.jsp" %>
    <script type="text/javascript">

        $(function() {
            getProviders();
            getConsumers();
        });

        function showEditDialog(provider) {
            $("#provider").val(provider);
            $.ajax({
                url: "${context}/getprovider",
                data:{service:"${service}", provider:provider},
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    $("#priority").val(data.result);
                    $('#editDialog').modal('show');
                }
            });

        }

        function setPriority() {
            $.ajax({
                url: "${context}/setpriority",
                data:{service:"${service}", provider:$("#provider").val(), priority:$("#priority").val()},
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    $('#editDialog').modal('hide');
                }
            });
        }

        function showChartDialog(provider) {
            $.ajax({
                url:"${context}/getchart",
                type:'post',
                dataType:'json',
                data:{service:"${service}", provider:provider},
                success:function(result) {
                    if(result.success) {
                        var myChart = echarts.init(document.getElementById('chart'), "macarons");
                        option = {
                            legend: {
                                data:result.result.legends
                            },
                            tooltip: {
                                trigger: 'axis'
                            },
                            title: {
                                text: result.result.title,
                            },
                            calculable : true,
                            xAxis: {
                                type: 'category',
                                boundaryGap: false,
                                data: result.result.date
                            },
                            yAxis: {
                                type: 'value'
                            },
                            dataZoom: [{
                                start: 0, end: result.result.zoom
                            }],
                            series: result.result.series
                        };
                        myChart.setOption(option);
                    }
                }
            });
            $('#chartDialog').modal('show');
        }

        function getProviders() {
            $.ajax({
                url: "${context}/getproviders?service=${service}",
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    var html = "";
                    for(var i = 0; i < data.result.length; i++) {
                        html += "<tr>";
                        html += "<td>" + data.result[i].name + "</td>";
                        html += "<td>";
                        html += "   <a href='javascript:showChartDialog(\"" + data.result[i].name + "\");'>查看</a>";
                        html += "&nbsp;&nbsp;";
                        html += "   <a href='javascript:showEditDialog(\"" + data.result[i].name + "\");'>修改</a>";
                        html += "</td>";
                        html += "</tr>";
                    }
                    $("#providerBody").html(html);
                }
            });
        }

        function getConsumers() {
            $.ajax({
                url: "${context}/getconsumers?service=${service}",
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    var html = "";
                    for(var i = 0; i < data.result.length; i++) {
                        html += "<tr>";
                        html += "<td>" + data.result[i].name + "</td>";
                        html += "<td><a href='javascript:showDialog(\"" + data.result[i].name + "\");'>修改</a></td>";
                        html += "</tr>";
                    }
                    $("#consumerBody").html(html);
                }
            });
        }

    </script>
</head>
<body>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <a href="${context}/service">服务列表</a>&nbsp;&nbsp;&gt;&gt;&nbsp;&nbsp;${service}
        </div>
        <div class="col-md-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    提供者列表
                </div>
                <div class="panel-body">
                    <table class="table table-striped table-bordered table-hover">
                        <tr class="info">
                            <th>name</th>
                            <th width="120px"></th>
                        </tr>
                        <tbody id="providerBody">
                        </tbody>
                    </table>
                </div>
            </div>
        </div>


        <div class="col-md-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    消费者列表
                </div>
                <div class="panel-body">
                    <table class="table table-striped table-bordered table-hover">
                        <tr class="info">
                            <th>name</th>
                            <th width="120px"></th>
                        </tr>
                        <tbody id="consumerBody">
                        </tbody>
                    </table>
                </div>
            </div>
        </div>


        <div class="modal fade" id="chartDialog">
            <div class="modal-dialog">
                <div class="modal-content" style="width:1000px;height:600px;">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">图表</h4>
                    </div>
                    <div class="modal-body">
                        <div id="chart" style="width:1000px;height:400px;"></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>


        <div class="modal fade" id="editDialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">编辑</h4>
                    </div>
                    <div class="modal-body">
                        <form>
                            <input type="hidden" id="provider">
                            <div class="form-group">
                                <label for="priority">优先级(1-10)：</label>
                                <input type="text" class="form-control" id="priority"/>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" onclick="setPriority()">确定</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>


    </div>
</div>

</body>
</html>
