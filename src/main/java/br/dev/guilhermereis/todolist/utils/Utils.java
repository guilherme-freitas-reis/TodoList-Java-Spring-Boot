package br.dev.guilhermereis.todolist.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

public class Utils {
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        var pds = src.getPropertyDescriptors();
        var emptyNames = new HashSet<>();

        for (var pd: pds) {
            var srcValue = src.getPropertyValue(pd.getName());

            if(srcValue == null) emptyNames.add(pd.getName());
        }

        var result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
