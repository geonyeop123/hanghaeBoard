package hanghaeboard.constants;

import lombok.Getter;

@Getter
public enum UserValidation {
    USERNAME(4, 10, "[a-z0-9]+"),
    PASSWORD(8, 15, "[a-zA-Z0-9]+");

    private final int min;
    private final int max;
    private final String regexp;

    UserValidation(int min, int max, String regexp) {
        this.min = min;
        this.max = max;
        this.regexp = regexp;
    }


}
