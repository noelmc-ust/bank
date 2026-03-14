package unaldi.invoiceservice.security;

import java.util.Arrays;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static boolean isAdmin(String rolesHeader) {
        if (rolesHeader == null) return false;
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
    }

    public static boolean isOwner(String headerUserId, Long pathUserId) {
        if (headerUserId == null || pathUserId == null) return false;
        return headerUserId.equals(String.valueOf(pathUserId));
    }
}