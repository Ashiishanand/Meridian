<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Register | Meridian</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-wrap">
  <div class="auth-card">
    <img class="logo" src="${pageContext.request.contextPath}/images/logo.png" alt="Meridian logo">
    <h1>Create your account</h1>

    <c:if test="${not empty error}">
      <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
      <label for="name">Full name</label>
      <input type="text" id="name" name="name" value="<c:out value='${name}'/>" required autofocus>

      <label for="email">Email</label>
      <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required>

      <label for="password">Password</label>
      <input type="password" id="password" name="password" required>

      <label for="confirm">Confirm password</label>
      <input type="password" id="confirm" name="confirm" required>

      <button type="submit" class="btn btn-block">Sign up</button>
    </form>

    <div class="auth-foot">Already registered? <a href="${pageContext.request.contextPath}/login">Log in</a></div>
    <div class="tagline">Make Time. Move Forward.</div>
  </div>
</div>
</body>
</html>
