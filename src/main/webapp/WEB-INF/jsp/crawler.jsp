<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>kinITic Web crawler</title>
</head>
<body>
        <form action="sitemap" method="POST" onsubmit="document.getElementById('loading').style.display='block'">
            <table width="50%">
                <tr>
                    <td width="48%">Enter Domain to crawl: </td>
                    <td width="52%">
                        <input id="domain-field" type="text" name="domain" />
                    </td>
                </tr>
            </table>
            <p>
                <input id="submit" type="submit" name="Submit" value="Start crawling..." />
            </p>
            <img id="loading" src="resources/loading.gif" style="display: none; position: fixed; top: 20%; left: 50%" />
        </form>
</body>
</html>
