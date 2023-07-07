package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class Relating {
    public static final String LINK_TO = "linkTo";
    public static final String LINK_FROM = "linkFrom";

    private List<String> linkTo;
    private List<String> linkFrom;

    public Relating(Object o) {
        linkTo = new ArrayList<>();
        linkFrom = new ArrayList<>();
        if (o instanceof Map) {
            Map m = (Map)o;

            if (m.containsKey(LINK_TO) && m.get(LINK_TO) instanceof Collection) {
                for (Object oo: (Collection) m.get(LINK_TO)) {
                    if (oo instanceof String) {
                        linkTo.add((String)oo);
                    }
                }
            }

            if (m.containsKey(LINK_FROM) && m.get(LINK_FROM) instanceof Collection) {
                for (Object oo: (Collection) m.get(LINK_FROM)) {
                    if (oo instanceof String) {
                        linkFrom.add((String)oo);
                    }
                }
            }
        }
    }

    public Relating() {
        linkTo = new ArrayList<>();
        linkFrom = new ArrayList<>();
    }

    public static void compare(List<String> newList, List<String> oldList, List<String> addList, List<String> delList) {
        for (String i : newList) {
            boolean found = false;
            for (String j : oldList) {
                if (i.equals(j)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                addList.add(i);
            }
        }

        for (String i : oldList) {
            boolean found = false;
            for (String j : newList) {
                if (i.equals(j)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                delList.add(i);
            }
        }
    }
}
