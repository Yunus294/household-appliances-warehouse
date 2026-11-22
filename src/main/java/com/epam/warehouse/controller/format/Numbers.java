package com.epam.warehouse.controller.format;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * How numbers are printed.
 */
public final class Numbers {

    private Numbers() {
    }

    /**
     * @return the amount with thousands separators: 12450 → "12,450"
     */
    public static String money(long amount) {
        return String.format(Locale.US, "%,d", amount);
    }

    /**
     * @return the number without trailing zeros: 8.0 → "8", 15.60 → "15.6"
     */
    public static String decimal(double number) {
        return BigDecimal.valueOf(number).stripTrailingZeros().toPlainString();
    }
}
