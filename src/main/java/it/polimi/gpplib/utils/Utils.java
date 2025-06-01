package it.polimi.gpplib.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Returns a list of CPVs from noticeCpvs that match (or are a child of) any of
     * the criteriaCpvs.
     * A match is either exact or noticeCpv starts with criteriaCpv (with trailing
     * zeros removed).
     */
    public static List<String> matchingCpvs(List<String> noticeCpvs, List<String> criteriaCpvs) {
        List<String> matchingCpvs = new ArrayList<>();
        for (String noticeCpv : noticeCpvs) {
            for (String criteriaCpv : criteriaCpvs) {
                if (noticeCpv.equals(criteriaCpv)) {
                    matchingCpvs.add(noticeCpv);
                    break;
                }
                // remove trailing zeros to look for child CPVs
                String trimmedCriteriaCpv = criteriaCpv.replaceAll("0+$", "");
                if (!trimmedCriteriaCpv.isEmpty() && noticeCpv.startsWith(trimmedCriteriaCpv)) {
                    matchingCpvs.add(noticeCpv);
                    break;
                }
            }
        }
        return matchingCpvs;
    }

    /**
     * Returns true if there is at least one matching CPV between the two lists.
     */
    public static boolean hasMatchingCpvs(List<String> noticeCpvs, List<String> criteriaCpvs) {
        return !matchingCpvs(noticeCpvs, criteriaCpvs).isEmpty();
    }
}
