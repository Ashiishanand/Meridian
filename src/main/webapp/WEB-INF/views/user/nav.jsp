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
  <a href="${ctx}/user/dashboard" class="${param.active == 'dashboard' ? 'active' : ''}">My Progress</a>
  <a href="${ctx}/user/goals" class="${param.active == 'goals' ? 'active' : ''}">My Goals</a>
  <a href="${ctx}/user/time-logs" class="${param.active == 'timelogs' ? 'active' : ''}">Time Logs</a>
</div>
<c:if test="${not empty sessionScope.userMsg}">
  <div class="container flash"><div class="alert alert-ok"><c:out value="${sessionScope.userMsg}"/></div></div>
  <c:remove var="userMsg" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.userError}">
  <div class="container flash"><div class="alert alert-error"><c:out value="${sessionScope.userError}"/></div></div>
  <c:remove var="userError" scope="session"/>
</c:if>
<c:if test="${not empty error}">
  <div class="container flash"><div class="alert alert-error"><c:out value="${error}"/></div></div>
</c:if>
