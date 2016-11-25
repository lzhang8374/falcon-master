<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>服务列表</title>
    <%@ include file="common/include.jsp" %>
    <script type="text/javascript">

        $(function() {
            getServices();
        });

        function showDialog() {
            $('#serviceDialog').modal('show');
        }

        function getServices() {
            $.ajax({
                url: "${context}/getservices",
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    var html = "";
                    for(var i = 0; i < data.result.length; i++) {
                        html += "<tr>";
                        html += "<td><a href='${context}/detail?service=" + data.result[i].name + "'>" + data.result[i].name + "</a></td>";
                        html += "<td><a href='javascript:showDialog(\"" + data.result[i].name + "\");'>修改</a></td>";
                        html += "</tr>";
                    }
                    $("#serviceBody").html(html);
                }
            });
        }
    </script>
</head>
<body>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    服务列表
                </div>
                <div class="panel-body">
                    <form class="form-inline">
                        <div class="form-group">
                            服务名：<input type="text" class="form-control" id="clientIdSearch">
                        </div>
                        <button type="button" class="btn btn-info" onclick="getServices();">搜索</button>
                    </form>
                    <br/>
                    <table class="table table-striped table-bordered table-hover">
                        <tr class="info">
                            <th>name</th>
                            <th width="60px"></th>
                        </tr>
                        <tbody id="serviceBody">
                        </tbody>
                    </table>
                </div>
            </div>
        </div>


        <div class="modal fade" id="serviceDialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="modalTitle"></h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal" method="post">
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>


    </div>
</div>

</body>
</html>
