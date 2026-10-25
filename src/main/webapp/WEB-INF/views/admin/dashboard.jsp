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
<jsp:include page="/WEB-INF/views/admin/nav.jsp">
  <jsp:param name="active" value="dashboard"/>
</jsp:include>

<div class="container">
  <div class="hero">
    <div>
      <h2>Usage Overview</h2>
      <p>How Meridian is being used across all accounts.</p>
    </div>
    <c:if test="${empty error}">
      <div class="hero-badge">
        <span class="big">${activeUsers} / ${totalUsers}</span>
        <span class="small">Active users</span>
      </div>
    </c:if>
  </div>

  <c:if test="${empty error}">
    <div class="stats">
      <div class="stat s-navy"><div class="icon">👥</div><div><div class="num">${totalUsers}</div><div class="lbl">Registered users</div></div></div>
      <div class="stat s-green"><div class="icon">✅</div><div><div class="num">${activeUsers}</div><div class="lbl">Active accounts</div></div></div>
      <div class="stat s-orange"><div class="icon">🎯</div><div><div class="num">${totalGoals}</div><div class="lbl">Goals set</div></div></div>
      <div class="stat s-navy"><div class="icon">📝</div><div><div class="num">${totalEntries}</div><div class="lbl">Time entries</div></div></div>
      <div class="stat s-orange"><div class="icon">⏱️</div><div><div class="num">${totalHours}</div><div class="lbl">Hours logged</div></div></div>
    </div>

    <div class="chart-grid">
      <div class="panel">
        <h3>Logins in the last 7 days</h3>
        <div class="chart-box"><canvas id="loginChart"></canvas></div>
      </div>
      <div class="panel">
        <h3>Activity breakdown</h3>
        <c:if test="${hasActions}">
          <div class="chart-box"><canvas id="actionChart"></canvas></div>
        </c:if>
        <c:if test="${not hasActions}">
          <p class="empty">No activity recorded yet.</p>
        </c:if>
      </div>
    </div>

    <div class="grid shortcuts">
      <a class="card" href="${pageContext.request.contextPath}/admin/goal-types">
        <h3>⚙️ Goal Parameters</h3>
        <p>Add goal types and choose how progress is tracked.</p>
      </a>
      <a class="card" href="${pageContext.request.contextPath}/admin/users">
        <h3>👤 User Accounts</h3>
        <p>Create accounts, activate, deactivate or remove users.</p>
      </a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    <script>
      new Chart(document.getElementById('loginChart'), {
        type: 'bar',
        data: {
          labels: ${loginLabels},
          datasets: [{ label: 'Logins', data: ${loginValues}, backgroundColor: '#e8941a', borderRadius: 6 }]
        },
        options: {
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
      });

      <c:if test="${hasActions}">
      new Chart(document.getElementById('actionChart'), {
        type: 'doughnut',
        data: {
          labels: ${actionLabels},
          datasets: [{ data: ${actionValues}, backgroundColor: ['#0b2545', '#e8941a', '#13315c', '#c97d0e', '#6b7785', '#1e7b45'] }]
        },
        options: { maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
      });
      </c:if>
    </script>
  </c:if>
</div>
<div class="footer">Meridian &middot; Make Time. Move Forward.</div>
</body>
</html>
