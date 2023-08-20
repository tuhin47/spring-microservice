package me.tuhin47.auth.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.tuhin47.auth.model.Role;
import me.tuhin47.auth.model.User;
import me.tuhin47.auth.repo.RoleRepository;
import me.tuhin47.auth.repo.UserRepository;
import me.tuhin47.auth.security.oauth2.SocialProvider;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private boolean alreadySetup = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		if (alreadySetup) {
			return;
		}
		Role userRole = createRoleIfNotFound(Role.ROLE_USER);
		Role adminRole = createRoleIfNotFound(Role.ROLE_ADMIN);
		Role modRole = createRoleIfNotFound(Role.ROLE_MODERATOR);
		createUserIfNotFound("admin@tuhin47.com", Set.of(userRole, adminRole, modRole));
		alreadySetup = true;
	}

	@Transactional
	public User createUserIfNotFound(final String email, Set<Role> roles) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			user = new User();
			user.setDisplayName(email);
			user.setEmail(email);
			user.setPassword(passwordEncoder.encode("admin@").getBytes());
			user.setRoles(roles);
			user.setProvider(SocialProvider.LOCAL.getProviderType());
			user.setEnabled(true);
			user = userRepository.save(user);
		}
		return user;
	}

	@Transactional
	public Role createRoleIfNotFound(final String name) {
		Role role = roleRepository.findByName(name);
		if (role == null) {
			role = roleRepository.save(new Role(name));
		}
		return role;
	}
}
