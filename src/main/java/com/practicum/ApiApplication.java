package com.practicum;

import com.practicum.config.ApplicationConfig;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.h2.server.web.JakartaWebServlet;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class ApiApplication {
    private static final int TOMCAT_PORT = 8080;

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.getConnector().setPort(TOMCAT_PORT);
        AnnotationConfigWebApplicationContext context =
                new AnnotationConfigWebApplicationContext();
        context.register(ApplicationConfig.class);
        context.getEnvironment().setActiveProfiles("h2");

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        tomcat.addContext(contextPath, docBase);

        Wrapper dispatcher = tomcat.addServlet(
                contextPath,
                "dispatcher",
                dispatcherServlet
        );
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");

        tomcat.start();
        System.out.println("Сервер запущен на http://localhost:" + TOMCAT_PORT);
        tomcat.getServer().await();
    }
}
