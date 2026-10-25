<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Admin Dashboard | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="topbar">
  <div class="brand">
    <img src="${pageContext.request.contextPath}/images/logo.png" alt="Meridian">
    <span>Make Time. Move Forward.</span>
  </div>
  <div class="who">
    <span><c:out value="${sessionScope.currentUser.name}"/> <span class="badge">${sessionScope.currentUser.roleLabel}</span></span>
    <a class="btn btn-outline" href="${pageContext.request.contextPath}/logout">Log out</a>
  </div>
</div>

<div class="container">
  <h2>Admin Dashboard</h2>
  <p class="sub">Configure the tool, manage accounts and oversee usage.</p>

  <div class="grid">
    <div class="card">
      <h3>Configure Goal Parameters</h3>
      <p>Form for goal types and tracking metrics.</p>
    </div>
    <div class="card">
      <h3>Manage User Accounts</h3>
      <p>Table listing every account with activate / deactivate.</p>
    </div>
    <div class="card">
      <h3>Oversee Tool Usage</h3>
      <p>Graphs of activity and usage metrics.</p>
    </div>
  </div>
</div>
<div class="footer">Meridian &middot; Make Time. Move Forward.</div>
</body>
</html>
