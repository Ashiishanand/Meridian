<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Login | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-wrap">
  <div class="auth-card">
    <img class="logo" src="${pageContext.request.contextPath}/images/logo.png" alt="Meridian logo">
    <h1>Welcome back</h1>
    <c:if test="${not empty sessionScope.flash}">
      <div class="alert alert-ok"><c:out value="${sessionScope.flash}"/></div>
      <c:remove var="flash" scope="session"/>
    </c:if>
    <c:if test="${not empty error}">
      <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
      <label for="email">Email</label>
      <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required autofocus>

      <label for="password">Password</label>
      <input type="password" id="password" name="password" required>

      <button type="submit" class="btn btn-block">Log in</button>
    </form>

    <div class="auth-foot">New to Meridian? <a href="${pageContext.request.contextPath}/register">Create an account</a></div>
    <div class="tagline">Make Time. Move Forward.</div>
  </div>
</div>
</body>
</html>
