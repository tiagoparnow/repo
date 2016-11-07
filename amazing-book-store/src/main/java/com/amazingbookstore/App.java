package com.amazingbookstore;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        
        EntityManagerFactory factory = Persistence.
                createEntityManagerFactory("amazingbooksotreUnit");

          factory.close();
    }
}
