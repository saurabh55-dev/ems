package ems

import grails.gorm.transactions.Transactional
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Transactional
class UserManagerService implements UserDetailsService {

    def passwordEncoder  // Grails style injection

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = User.findByUsername(username)
        if (!user) throw new UsernameNotFoundException('User not found')

        new org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            user.enabled,
            !user.accountExpired,
            !user.passwordExpired,
            !user.accountLocked,
            user.authorities
        )
    }
}