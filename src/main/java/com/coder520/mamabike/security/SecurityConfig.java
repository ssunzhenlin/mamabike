package com.coder520.mamabike.security;

import com.coder520.mamabike.cache.CommonCacheUtil;
import com.coder520.mamabike.common.constants.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by JackWangon[www.aiprogram.top] 2017/7/29.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Parameters parameters;
    @Autowired
    private CommonCacheUtil commonCacheUtil;



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 为验证拦截器设置AuthenticationManager (由于用了springboot注入方式)
     * @return
     * @throws Exception
     */
    private RestPreAuthenticatedProcessingFilter getPreAuthenticatedProcessingFilter() throws Exception {
        RestPreAuthenticatedProcessingFilter filter = new RestPreAuthenticatedProcessingFilter(parameters.getNoneSecurityPath(),commonCacheUtil);
        filter.setAuthenticationManager(this.authenticationManagerBean());
        return  filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(new RestAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(parameters.getNoneSecurityPath().toArray(new String[parameters.getNoneSecurityPath().size()])).permitAll()//符合条件的路径放过验证
//                .anyRequest().hasRole("BIKE_CLIENT")//其他全部需要 BIKE_CLIENT 角色
                .anyRequest().authenticated()//其他全部需要授权
                .and().httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())//设置统一信息返回处理
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//无状态请求 不需要session
                .and()
                .addFilter(getPreAuthenticatedProcessingFilter())//添加自定义登录验证过滤器
                ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")//忽略 OPTIONS 方法的请求
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**");
     //放过swagger
    }

}
