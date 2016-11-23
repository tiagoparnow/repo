package com.amazingbookstore.util;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Typed;

@Typed
public abstract class ProxyUtils {
    
	
	private ProxyUtils(){}
    
	public static Class<?> getUnproxiedClass(Class<?> currentClass) {
        Class<?> unproxiedClass = currentClass;
        
        while (isProxiedClass(unproxiedClass)){
            unproxiedClass = unproxiedClass.getSuperclass();
        }

        return unproxiedClass;
    }

    public static boolean isProxiedClass(Class<?> currentClass) {
        if (currentClass == null || currentClass.getSuperclass() == null){
            return false;
        }

        return currentClass.getName().startsWith(currentClass.getSuperclass().getName()) && (currentClass.getName().contains("$$") || currentClass.getName().contains("_"));
    }
    
    public static List<Class<?>> getProxyAndBaseTypes(Class<?> proxyClass){
    	final List<Class<?>> result = new ArrayList<Class<?>>();
        result.add(proxyClass);
        
        if (isInterfaceProxy(proxyClass)){
            for (Class<?> currentInterface : proxyClass.getInterfaces()){
                if (proxyClass.getName().startsWith(currentInterface.getName())){
                    result.add(currentInterface);
                }
            }
            
        } else {
            Class<?> unproxiedClass = proxyClass.getSuperclass();
            result.add(unproxiedClass);

            while(isProxiedClass(unproxiedClass)){
                unproxiedClass = unproxiedClass.getSuperclass();
                result.add(unproxiedClass);
            }
        }
        
        return result;
    }

    public static boolean isInterfaceProxy(Class<?> proxyClass) {
        final Class<?>[] interfaces = proxyClass.getInterfaces();
        if (Proxy.class.equals(proxyClass.getSuperclass()) && interfaces != null && interfaces.length > 0) {
            return true;
        }
        
        if (proxyClass.getSuperclass() != null && !proxyClass.getSuperclass().equals(Object.class)) {
            return false;
        }
        
        if (proxyClass.getName().contains("$$") || proxyClass.getName().contains("_")) {
            for (Class<?> currentInterface : interfaces) {
                if (proxyClass.getName().startsWith(currentInterface.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

}