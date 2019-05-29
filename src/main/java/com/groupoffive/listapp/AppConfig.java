package com.groupoffive.listapp;

import com.groupoffive.listapp.controllers.*;
import com.groupoffive.listapp.routers.*;
import com.groupoffive.listapp.util.AmazonQueue;
import com.groupoffive.listapp.util.Crypt;
import com.groupoffive.listapp.util.CryptSha256;
import com.groupoffive.listapp.util.FirebaseNotificationService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

@Configuration
public class AppConfig {

    public static AnnotationConfigApplicationContext getContext() {
        return new AnnotationConfigApplicationContext(AppConfig.class);
    }

    public static EntityManager getEntityManager() {
        return Persistence.createEntityManagerFactory("mydb").createEntityManager();
    }

    @Bean
    public Crypt cryptSha256() {
        return new CryptSha256();
    }

    @Bean
    public ListsRouter listsRouter() {
        return new ListsRouter(listsController());
    }

    @Bean
    public GroupsRouter groupsRouter() {
        return new GroupsRouter(groupsController());
    }

    @Bean
    public UsersRouter usersRouter() {
        return new UsersRouter(usersController());
    }

    @Bean
    public CategoriesRouter categoriesRouter() {
        return new CategoriesRouter(categoriesController());
    }

    @Bean
    public ProductsRouter productsRouter() {
        return new ProductsRouter(productsController());
    }

    @Bean
    ListsController listsController() { return new ListsController(this.firebaseNotificationService()); }

    @Bean
    GroupsController groupsController() {
        return new GroupsController();
    }

    @Bean
    UsersController usersController() { return new UsersController(this.cryptSha256(), this.firebaseNotificationService()); }

    @Bean
    CategoriesController categoriesController() {
        return new CategoriesController();
    }

    @Bean
    ProductsController productsController() { return new ProductsController(this.amazonQueue()); }

    @Bean
    FirebaseNotificationService firebaseNotificationService() { return new FirebaseNotificationService(); }

    @Bean
    AmazonQueue amazonQueue() { return new AmazonQueue(); }

}
