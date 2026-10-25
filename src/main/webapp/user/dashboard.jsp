<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>My Dashboard | Meridian</title>
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
  <h2>Hello, <c:out value="${sessionScope.currentUser.name}"/> 👋</h2>
  <p class="sub">Set goals, log your time, and watch your progress.</p>

  <div class="grid">
    <div class="card">
      <h3>Set Personal Goals</h3>
      <p>Form for goal name, target and deadline.</p>
    </div>
    <div class="card">
      <h3>Track Time Towards Goals</h3>
      <p>Log start/end time and see your time logs in a table.</p>
    </div>
    <div class="card">
      <h3>Monitor Progress</h3>
      <p>Summary of goal progress and achievements.</p>
    </div>
  </div>
</div>
<div class="footer">Meridian &middot; Make Time. Move Forward.</div>
</body>
</html>
