package easyenterprise.lib.util;

public class Tuple<A, B> implements Comparable<Tuple<A, B>> {
    private final A first;
    private final B second;

    public static <A, B> Tuple<A, B> create(A first, B second) {
    	return new Tuple<A, B>(first, second);
    }
    
    public Tuple(A first, B second) {
		this.first = first;
		this.second = second;
    }

    public A getFirst() { return first; }
    public B getSecond() { return second; }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    private static boolean equals(Object x, Object y) {
    	return (x == null && y == null) || (x != null && x.equals(y));
    }

    @SuppressWarnings("unchecked")
	public boolean equals(Object other) {
    	return
	    other instanceof Tuple &&
	    equals(first, ((Tuple<A, B>)other).first) &&
	    equals(second, ((Tuple<A, B>)other).second);
    }

    public int hashCode() {
		if (first == null) return (second == null) ? 0 : second.hashCode() + 1;
		else if (second == null) return first.hashCode() + 2;
		else return first.hashCode() * 17 + second.hashCode();
    }

    @Override
		@SuppressWarnings("unchecked")
		public int compareTo(Tuple<A, B> other) {
			int result = ((Comparable<A>) first).compareTo((A) other.first);
			if (result == 0) 
				result = ((Comparable<B>) second).compareTo((B) other.second);
			return result;
		}
}