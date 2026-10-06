<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registration Result - JSP & Servlet Architecture</title>
    <style>
        :root {
            --primary: #4361ee;
            --primary-hover: #3a56d4;
            --success: #10b981;
            --card-bg: #ffffff;
            --text-main: #1e293b;
            --text-muted: #64748b;
            --border: #e2e8f0;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
        }

        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f0fdf4 0%, #f0f4ff 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .card {
            background: var(--card-bg);
            border-radius: 12px;
            padding: 32px;
            max-width: 460px;
            width: 100%;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
            border: 1px solid var(--border);
        }

        .badge-success {
            display: inline-block;
            font-size: 0.75rem;
            text-transform: uppercase;
            font-weight: 700;
            color: #065f46;
            background: #d1fae5;
            padding: 4px 10px;
            border-radius: 9999px;
            margin-bottom: 12px;
        }

        h2 {
            color: var(--text-main);
            margin-bottom: 6px;
            font-size: 1.5rem;
        }

        p.subtitle {
            color: var(--text-muted);
            font-size: 0.875rem;
            margin-bottom: 20px;
        }

        .result-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 24px;
        }

        .result-table tr {
            border-bottom: 1px solid var(--border);
        }

        .result-table tr:last-child {
            border-bottom: none;
        }

        .result-table td {
            padding: 12px 6px;
            font-size: 0.95rem;
        }

        .result-table td.label {
            font-weight: 600;
            color: var(--text-muted);
            width: 35%;
        }

        .result-table td.value {
            color: var(--text-main);
            font-weight: 500;
        }

        .btn-back {
            display: block;
            text-align: center;
            width: 100%;
            padding: 12px;
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 0.95rem;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            transition: background 0.2s;
        }

        .btn-back:hover {
            background-color: var(--primary-hover);
        }

        .footer-note {
            margin-top: 20px;
            font-size: 0.8rem;
            color: var(--text-muted);
            text-align: center;
            border-top: 1px solid var(--border);
            padding-top: 14px;
        }
    </style>
</head>
<body>

    <div class="card">
        <span class="badge-success">&check; Processed via HelloServlet</span>
        <h2>Registration Successful</h2>
        <p class="subtitle">Attributes received in request scope & rendered via JSP EL:</p>

        <table class="result-table">
            <tr>
                <td class="label">Full Name</td>
                <td class="value">${name}</td>
            </tr>
            <tr>
                <td class="label">Email</td>
                <td class="value">${email}</td>
            </tr>
            <tr>
                <td class="label">Course</td>
                <td class="value">${course}</td>
            </tr>
            <tr>
                <td class="label">Age</td>
                <td class="value">${age}</td>
            </tr>
        </table>

        <a href="student.jsp" class="btn-back">&larr; Register Another Student</a>

        <div class="footer-note">
            Rendered by: <code>result.jsp</code> using <code>${'${...}'}</code> EL tags
        </div>
    </div>

</body>
</html>
