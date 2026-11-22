package com.epam.warehouse.controller.argument;

import com.epam.warehouse.entity.Category;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The word after the command that says where to look: a category ({@code laptops}, or
 * {@code laptop}) or {@code all}.
 */
public final class Targets {

    private static final String ALL = "all";
    private static final String PLURAL_ENDING = "s";

    private Targets() {
    }

    /**
     * @throws InvalidRequestException if the word is neither a category nor "all"
     */
    public static Set<Category> resolve(String target) throws InvalidRequestException {
        String word = target.strip().toLowerCase(Locale.ROOT);
        if (ALL.equals(word)) {
            return EnumSet.allOf(Category.class);
        }
        String singular = word.endsWith(PLURAL_ENDING) ? word.substring(0, word.length() - 1) : word;
        return Category.byKey(singular)
                .map(EnumSet::of)
                .orElseThrow(() -> new InvalidRequestException("Unknown target '" + target + "'. Use " + choices()));
    }

    /**
     * @return how a category is written in commands, e.g. "laptops"
     */
    public static String nameOf(Category category) {
        return category.getKey() + PLURAL_ENDING;
    }

    /**
     * @return "laptops, ovens, refrigerators, washing-machines or all"
     */
    public static String choices() {
        return Arrays.stream(Category.values()).map(Targets::nameOf).collect(Collectors.joining(", "))
                + " or " + ALL;
    }

    /**
     * @return "laptops", "laptops, ovens" or "any category"
     */
    public static String describe(Set<Category> categories) {
        if (categories.size() == Category.values().length) {
            return "any category";
        }
        return categories.stream().map(Targets::nameOf).collect(Collectors.joining(", "));
    }
}
