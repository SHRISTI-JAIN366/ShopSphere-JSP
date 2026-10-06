<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Registration - JSP & Servlet Architecture</title>
    <style>
        :root {
            --primary: #4361ee;
            --primary-hover: #3a56d4;
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
            background: linear-gradient(135deg, #f0f4ff 0%, #e2e8f0 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .card {
            background: var(--card-bg);
            border-radius: 12px;
            padding: 32px;
            max-width: 440px;
            width: 100%;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
            border: 1px solid var(--border);
        }

        .badge {
            display: inline-block;
            font-size: 0.75rem;
            text-transform: uppercase;
            font-weight: 700;
            color: var(--primary);
            background: #eef2ff;
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
            margin-bottom: 24px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 600;
            font-size: 0.85rem;
            color: #334155;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border: 1.5px solid var(--border);
            border-radius: 6px;
            font-size: 0.95rem;
            outline: none;
            transition: border-color 0.2s;
            background: #ffffff;
        }

        input:focus, select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.15);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            margin-top: 10px;
            transition: background 0.2s;
        }

        .btn-submit:hover {
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
        <span class="badge">JSP View &rarr; Servlet</span>
        <h2>Student Registration</h2>
        <p class="subtitle">Submit form details to <code>HelloServlet</code> via HTTP POST.</p>

        <form action="hello" method="post">
            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" placeholder="Enter full name" required>
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" placeholder="student@example.com" required>
            </div>

            <div class="form-group">
                <label for="course">Select Course</label>
                <select id="course" name="course">
                    <option value="Java EE / Jakarta Servlets & JSP">Java EE / Jakarta Servlets & JSP</option>
                    <option value="Spring Boot & Microservices">Spring Boot & Microservices</option>
                    <option value="Full Stack Java Development">Full Stack Java Development</option>
                </select>
            </div>

            <div class="form-group">
                <label for="age">Age</label>
                <input type="number" id="age" name="age" min="1" max="120" placeholder="e.g. 21" required>
            </div>

            <button type="submit" class="btn-submit">Register Student &rarr;</button>
        </form>

        <div class="footer-note">
            Target: <code>@WebServlet("/hello")</code> &rarr; <code>result.jsp</code>
        </div>
    </div>

</body>
</html>
