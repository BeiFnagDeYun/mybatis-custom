package org.demo.acti.web.controller.login;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping("loginPage")
    public String loginPage() {
        StringBuilder buf = new StringBuilder();
        buf.append("<!DOCTYPE html>");
        buf.append("<html lang=\"en\">");
        buf.append("<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "    <meta name=\"description\" content=\"\">\n" +
                "    <meta name=\"author\" content=\"\">");
        buf.append("</head>");
        buf.append("<body>");
        buf.append(" <div style=\"display:none\" class=\"container\">\n" +
                "      <form class=\"form-signin\" id=\"loginForm\" method=\"post\" action=\"/login\">\n" +
                "        <h2 class=\"form-signin-heading\">Please sign in</h2>\n" +
                "        <p>\n" +
                "          <label for=\"username\" class=\"sr-only\">Username</label>\n" +
                "          <input type=\"text\" id=\"username\" name=\"username\" value=\"autoLogin\" class=\"form-control\" placeholder=\"Username\" required>\n" +
                "        </p>\n" +
                "        <p>\n" +
                "          <label for=\"password\" class=\"sr-only\">Password</label>\n" +
                "          <input type=\"password\" id=\"password\" name=\"password\" value=\"autoLogin\" class=\"form-control\" placeholder=\"Password\" required>\n" +
                "        </p>\n" +
                "           <input name=\"_csrf\" type=\"hidden\" value=\"77f6dbea-e1bf-4951-93de-364267959ce0\" />\n" +
                "        <button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Sign in</button>\n" +
                "      </form>\n" +
                "</div>");
        buf.append("</body>");
        buf.append("<script>");
        buf.append("var form = document.getElementById('loginForm');");
        buf.append("form.submit();");
        buf.append("</script>");
        buf.append("</html>");
        return buf.toString();
    }

}
