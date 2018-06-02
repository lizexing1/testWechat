<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<table border="1">
		<tr>
			<td>菜单名称</td>
			<td><input type="text" id="name" name="name" /></td>
		</tr>
		<tr>
			<td>菜单类型</td>
			<td><input type="text" id="type" name="type" /></td>
		</tr>
		<tr>
			<td>菜单地址</td>
			<td><input type="text" id="url" name="url" /></td>
		</tr>
		<tr>
			<td>父菜单</td>
			<td>
				<select id="parentMenu">
					<option value="0">一级菜单</option>
					<c:forEach items="${list}" var="model">
						<option value="${model.id}">${model.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="button" onclick="add()" value="添加" />
			</td>
		</tr>
	</table>

</body>
</html>