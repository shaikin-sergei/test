package fs.service.impl;

import fs.dao.UserRepository;
import fs.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsService")
class UserDetailsServiceImplTests {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Test loadUserByUsername with wrong username")
    void testLoadUserByUsernameWithWrongName() {
        String userName = "test";
        when(userRepository.findByUsername(userName)).thenReturn(null);
        assertThrows(
                UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(userName));
    }

    @Test
    @DisplayName("Test loadUserByUsername positive case")
    void testLoadUserByUsername() {
        String userName = "test", pass = "pass";
        User user = new User();
        user.setId(1L);
        user.setUsername(userName);
        user.setPassword(pass);

        when(userRepository.findByUsername(userName)).thenReturn(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        assertNotNull(userDetails);
        assertEquals(userName, userDetails.getUsername());
        assertEquals(pass, userDetails.getPassword());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isEnabled());
    }

}
