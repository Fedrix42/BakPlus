package utils;

/**
 * A Pair of two objects
 * @author Fedrix
 */
public record Pair<T1, T2>(T1 first, T2 second) {

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair{" + "" + first + "," + second + '}';
    }
    
    
    
}
