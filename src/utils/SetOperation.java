package utils;

/** interface for some mathematical operation on sets */
public interface SetOperation<T> {
    /**
     * intersection operation on such object and a given object
     * @param obj the given object
     * @return a new instance of {@code T}, with the intersection result as its value */
    T intersection(T obj);

    /**
     * union operation on such object and a given object
     * @param obj the given object
     * @return a new instance of {@code T}, with the union result as its value */
    T union(T obj);

    /**
     * subtraction operation on such object by a given object
     * @param obj the given object
     * @return a new instance of {@code T}, with value as (this object \ given object)
     */
    T substraction(T obj);
}
