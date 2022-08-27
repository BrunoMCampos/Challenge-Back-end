package br.com.alura.financialmanagement.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.financialmanagement.model.User;
import br.com.alura.financialmanagement.repository.UserRepository;

public class AuthenticationByTokenFilter extends OncePerRequestFilter {

	private TokenService tokenService;

	private UserRepository userRepository;

	public AuthenticationByTokenFilter(TokenService tokenService, UserRepository userRepository) {
		this.tokenService = tokenService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = getToken(request);
		boolean isValid = tokenService.isTokenValid(token);

		if (isValid) {
			authenticateClient(token);
		}

		filterChain.doFilter(request, response);

	}

	private void authenticateClient(String token) {
		Long userId = tokenService.getUserId(token);
		User user = userRepository.findById(userId).get();

		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getProfiles());
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}

	private String getToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7, token.length());
	}

}
