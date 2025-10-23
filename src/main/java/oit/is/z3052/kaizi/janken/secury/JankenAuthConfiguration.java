package oit.is.z3052.kaizi.janken.secury;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class JankenAuthConfiguration {

  /**
   * 認可処理に関する設定（認証されたユーザがどこにアクセスできるか）
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.formLogin(login -> login
        .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")) // ログアウト後に / にリダイレクト
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/janken/**").authenticated()
            .anyRequest().permitAll()) // 上記以外は全員アクセス可能
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/*", "/janken*/**")) // sample2用にCSRF対策を無効化
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                .sameOrigin()));
    return http.build();
  }

  /**
   * 認証処理に関する設定（誰がどのようなロールでログインできるか）
   *
   * @return
   */
  @Bean
  public InMemoryUserDetailsManager userDetailsService() {

    // ユーザ名，パスワード，ロールを指定してbuildする
    // このときパスワードはBCryptでハッシュ化されているため，{bcrypt}とつける
    // ハッシュ化せずに平文でパスワードを指定する場合は{noop}をつける
    // user1/p@ss,user2/p@ss,admin/p@ss

    UserDetails user1 = User.withUsername("user1")
        .password("{bcrypt}$2y$05$bBYHa8rLzxud24/5ZlPxTOEqREzSjTp7lXxPka7cfWwCqx0oZZR3a").roles("USER").build();
    UserDetails user2 = User.withUsername("user2")
        .password("{bcrypt}$2y$05$R9K1Vy6.NotHYKkuyfJq3eBm7jJlomO7BkYgq5osSTlLjOaDUcL8e").roles("USER").build();
    UserDetails ほんだ = User.withUsername("ほんだ")
        .password("{bcrypt}$2y$05$ZwS2A0eo4zEIW6jmGCYH0u3Xr63kJ0dc4tzz/3953ipOD4j8pTtYG").roles("USER").build();
    UserDetails admin = User.withUsername("admin")
        .password("{bcrypt}$2y$10$ngxCDmuVK1TaGchiYQfJ1OAKkd64IH6skGsNw1sLabrTICOHPxC0e").roles("ADMIN").build();

    // 生成したユーザをImMemoryUserDetailsManagerに渡す（いくつでも良い）
    return new InMemoryUserDetailsManager(user1, user2, ほんだ, admin);
  }

}
