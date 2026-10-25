<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="topbar">
  <div class="brand">
    <img src="${ctx}/images/logo.png" alt="Meridian">
    <span>Make Time. Move Forward.</span>
  </div>
  <div class="who">
    <span><c:out value="${sessionScope.currentUser.name}"/> <span class="badge">${sessionScope.currentUser.roleLabel}</span></span>
    <a class="btn btn-outline" href="${ctx}/logout">Log out</a>
  </div>
</div>
<div class="nav-tabs">
  <a href="${ctx}/admin/dashboard" class="${param.active == 'dashboard' ? 'active' : ''}">Usage Overview</a>
  <a href="${ctx}/admin/goal-types" class="${param.active == 'goaltypes' ? 'active' : ''}">Goal Parameters</a>
  <a href="${ctx}/admin/users" class="${param.active == 'users' ? 'active' : ''}">User Accounts</a>
</div>
<c:if test="${not empty sessionScope.adminMsg}">
  <div class="container flash"><div class="alert alert-ok"><c:out value="${sessionScope.adminMsg}"/></div></div>
  <c:remove var="adminMsg" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.adminError}">
  <div class="container flash"><div class="alert alert-error"><c:out value="${sessionScope.adminError}"/></div></div>
  <c:remove var="adminError" scope="session"/>
</c:if>
<c:if test="${not empty error}">
  <div class="container flash"><div class="alert alert-error"><c:out value="${error}"/></div></div>
</c:if>
