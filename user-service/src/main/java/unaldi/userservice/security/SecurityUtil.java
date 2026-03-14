public class SecurityUtil {
    
}
package unaldi.userservice.security;

import java.util.Arrays;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static boolean isAdmin(String rolesHeader) {
        if (rolesHeader == null) return false;
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
    }
}