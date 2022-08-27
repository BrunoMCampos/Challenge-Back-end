package br.com.alura.financialmanagement.config.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.alura.financialmanagement.model.User;
import br.com.alura.financialmanagement.repository.UserRepository;

@Service
public class AutenticacaoService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			return user.get();
		}

		throw new UsernameNotFoundException("Dados inválidos");
	}

}
