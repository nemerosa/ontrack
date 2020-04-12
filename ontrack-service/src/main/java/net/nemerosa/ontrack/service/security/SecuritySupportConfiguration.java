package net.nemerosa.ontrack.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// TODO #756 Disable custom security
//@Configuration
public class SecuritySupportConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        PasswordEncoder encoder = new SecuritySupportConfiguration().passwordEncoder();
        for (String arg : args) {
            System.out.format(
                    "%s ---> %s",
                    arg,
                    encoder.encode(arg)
            );
        }
    }
}
