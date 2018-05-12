package kingvalue;

import java.util.List;
import java.util.Locale;

/**
 * Class containing generic purpose methods.
 */
public class Misc {

    /**
     * Generates a string representing the given double.
     */
    public static String doubleToString(double d) {

        return String.format(Locale.US, "%.6f", d);
    }

    /**
     * Generates a string representing the given array of double.
     */
    public static String arrayToString(double[] array) {

        if (array.length == 0) return "[]";

        StringBuilder string = new StringBuilder("[" + doubleToString(array[0]));
        for (int i = 1; i < array.length; i++) string.append(", ").append(doubleToString(array[i]));
        return string + "]";
    }

    /**
     * Generates a string representing the given ArrayList of double.
     */
    public static String listToString(List<Double> array) {

        if (array.size() == 0) return "[]";

        StringBuilder string = new StringBuilder("[" + doubleToString(array.get(0)));
        for (int i = 1; i < array.size(); i++) string.append(", ").append(doubleToString(array.get(i)));
        return string + "]";
    }

    /**
     * Normalizes the array to have sum 1.
     */
    public static void normalizeArray(double[] array) {

        double sum = 0;
        for (double index : array) sum += index;
        if (sum > 0) for (int i = 0; i < array.length; i++) array[i] /= sum;
    }
}
