package com.app.utils.map;


/**
 * A strategy interface for comparing two values of the same type.
 *
 * <p>This functional interface defines a comparison contract that determines
 * whether two values satisfy a specific condition. It enables parameterization
 * of comparison algorithms, making code more flexible, reusable, and testable.</p>
 *
 * <p>The comparator is designed to be a pure function without side effects,
 * allowing it to be used safely in concurrent contexts and with stream operations.</p>
 *
 * <h3>Basic usage examples:</h3>
 * <pre>{@code
 * // Equality comparison
 * Comparator<String> equalsComparator = (expected, actual) -> expected.equals(actual);
 *
 * // Case-insensitive comparison for strings
 * Comparator<String> caseInsensitive = (e, a) -> e.equalsIgnoreCase(a);
 *
 * // Numeric comparison (e.g., within tolerance)
 * Comparator<Double> toleranceComparator = (e, a) -> Math.abs(e - a) < 0.01;
 *
 * // Custom object comparison
 * Comparator<User> userComparator = (expected, actual) ->
 *     expected.getId().equals(actual.getId());
 * }</pre>
 *
 * <h3>Usage in validation context:</h3>
 * <pre>{@code
 * public <T> void validateField(String fieldName,
 *                              T actual,
 *                              T expected,
 *                              Comparator<T> comparator) {
 *     if (!comparator.compare(expected, actual)) {
 *         throw new ValidationException(
 *             String.format("Field '%s' expected: %s, but was: %s",
 *                          fieldName, expected, actual)
 *         );
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of values to be compared
 */
@FunctionalInterface
public interface Comparator<T> {

    /**
     * Compares two values according to the implementation strategy.
     *
     * <p>Determines whether the actual value matches the expected value
     * based on the specific comparison logic defined by the implementation.</p>
     *
     * <p>Implementations should follow these conventions:</p>
     * <ul>
     *   <li>Be pure functions with no side effects</li>
     *   <li>Be thread-safe and stateless when possible</li>
     *   <li>Handle null values appropriately (either by throwing NullPointerException
     *       or by defining specific null-handling behavior)</li>
     *   <li>Document any specific behavior regarding null values or edge cases</li>
     * </ul>
     *
     * @param expected the reference value to compare against
     * @param actual   the value to check
     * @return {@code true} if the values satisfy the comparison condition,
     * {@code false} otherwise
     * @throws NullPointerException if the implementation does not support null values
     *                              and either parameter is null
     */
    boolean compare(T expected, T actual);
}
