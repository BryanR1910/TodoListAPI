package com.bryan.TodoListAPI.filter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket(){
        return Bucket.builder().
                addLimit(limit -> limit.capacity(5).refillGreedy(2, Duration.ofMinutes(10)))
                .build();
    }

    private Bucket resolveBucket(String key){
        return buckets.computeIfAbsent(key, k -> createBucket());
    }

    private String resolveKey(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)){
            return auth.getName();
        }

        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if(path.startsWith("/auth/")){
            System.out.println("no aplicar buckets");
            filterChain.doFilter(request,response);
            return;
        }

        String key = resolveKey(request);
        Bucket bucket = resolveBucket(key);

        if(bucket.tryConsume(1)){
            filterChain.doFilter(request,response);
        } else{
            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }

    }

}
