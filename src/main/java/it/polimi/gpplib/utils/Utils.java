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

    /**
     * Returns the content of the XML file as a String.
     */
    public static String loadXmlString(String filePath) {
        try (java.io.InputStream is = Utils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                System.err.println("Resource not found: " + filePath);
                return null;
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            System.err.println("Failed to load XML from resource: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
}
