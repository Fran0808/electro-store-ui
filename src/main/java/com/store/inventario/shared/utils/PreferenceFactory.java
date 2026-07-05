package com.store.inventario.shared.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferenceFactory {

    public static <T> Preferences create(Class<T> element){
        return Preferences.userNodeForPackage(element);
    }

    public static <T> String get(Class<T> element, String key){
        return Preferences.userNodeForPackage(element).get(key, null);
    }

    public static <T> void put(Class<T> element, String key, String value){
        Preferences.userNodeForPackage(element).put(key, value);
    }

    public static <T> void remove(Class<T> element, String key){
        Preferences.userNodeForPackage(element).remove(key);
    }

    public static <T> void flush(Class<T> element) {
        try {
            Preferences.userNodeForPackage(element).flush();
        } catch (BackingStoreException exception) {
            System.out.println("An error occurred while saving the application preferences: " + exception.getMessage());
        }
    }

}
