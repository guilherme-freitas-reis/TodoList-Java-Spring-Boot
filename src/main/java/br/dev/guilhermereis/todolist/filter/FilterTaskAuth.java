package br.dev.guilhermereis.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.dev.guilhermereis.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if(!servletPath.startsWith("/tasks/"))  {
            filterChain.doFilter(request, response);
            return;
        }

        var authorization = request.getHeader("Authorization");
        if(authorization == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        var authEncoded = authorization.substring("Basic".length()).trim();
        byte[] authDecode = Base64.getDecoder().decode(authEncoded);
        var authString = new String(authDecode);
        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];

        var user = this.userRepository.findByUsername(username);
        if (user == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!passwordVerify.verified) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        request.setAttribute("user", user);
        filterChain.doFilter(request, response);
    }
}
