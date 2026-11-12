package com.bertello.machinery_services.security;

import com.bertello.machinery_services.entity.UserEntity;
import com.bertello.machinery_services.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService,
                         @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
                         UserRepository userRepository) {
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userRepository = userRepository;
    }

    /**
     * Se ejecuta en cada solicitud HTTP para validar el token JWT ya creado.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);
            // Validamos el token y obtenemos el userId
            Long userId = jwtService.getUserIdFromToken(token);

            // Si hay usuario y aún no está autenticado, se autentica
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserEntity userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

                if (!userEntity.getEnabled()) throw new DisabledException("Usuario deshabilitado");

                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + userEntity.getRole())
                );

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        userEntity.getEmail(),
                        "",
                        userEntity.getEnabled(),
                        true, true, true,
                        authorities
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}